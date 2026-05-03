import { Component, OnInit, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { AiChatService, LinkedItem } from './ai-chat.service';
import { AuthService } from '../auth/auth.service';

interface Message {
  role: 'user' | 'assistant';
  content: string;
  displayContent?: string;
  items?: LinkedItem[];
  action?: any;
  timestamp: Date;
  hasFeedback?: boolean;
  canHaveFeedback?: boolean;
  isPositiveFeedback?: boolean;
  isStreaming?: boolean;
}

@Component({
  selector: 'app-ai-chat',
  templateUrl: './ai-chat.component.html',
  styleUrls: ['./ai-chat.component.css']
})
export class AiChatComponent implements OnInit, AfterViewChecked {
  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;
  @ViewChild('chatInput') private chatInput!: ElementRef;

  messages: Message[] = [];
  userInput: string = '';
  isLoading: boolean = false;
  private lastScrollTime = 0;

  upcomingTasks: any[] = [];
  memoryInsight: string = "Analyzing your activity...";
  relatedNote: any = { title: "None yet", lastEdited: "" };
  suggestedChips = [
    'What are my top priorities today?',
    'Summarize my recent notes',
    'Add a task to gym at 6pm',
    'Any patterns in my productivity?'
  ];

  constructor(
    private aiService: AiChatService,
    private authService: AuthService
  ) {}

  getUserInitial(): string {
    const user = this.authService.getUser();
    return user ? user.charAt(0).toUpperCase() : 'U';
  }

  getGreeting(): string {
    const hour = new Date().getHours();
    if (hour >= 5 && hour < 12) return 'Good morning!';
    if (hour >= 12 && hour < 17) return 'Good afternoon!';
    if (hour >= 17 && hour < 21) return 'Good evening!';
    return 'Good night!';
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  onInput() {
    const textarea = this.chatInput.nativeElement;
    textarea.style.height = 'auto';
    textarea.style.height = Math.min(textarea.scrollHeight, 200) + 'px';
  }

  ngOnInit() {
    this.loadHistory();
    this.loadSidebarData();
    this.refreshIcons();
  }

  private loadSidebarData() {
    this.aiService.getSidebarData().subscribe({
      next: (data) => {
        this.upcomingTasks = data.upcomingTasks || [];
        this.memoryInsight = data.memoryInsight || "No insights yet.";
        this.relatedNote = data.relatedNote || { title: "None yet", lastEdited: "" };
      },
      error: (err) => console.error('Failed to load sidebar data:', err)
    });
  }

  private loadHistory() {
    this.aiService.getHistory().subscribe({
      next: (history: any[]) => {
        if (history && history.length > 0) {
          this.messages = history.map(m => {
            let display = m.content;
            let items = [];
            let action = null;
            if (m.role === 'assistant') {
              try {
                const clean = this.extractJson(m.content);
                const parsed = JSON.parse(clean);
                display = parsed.message || m.content;
                items = parsed.items || [];
                action = parsed.action || null;
              } catch (e) {}
            }
            return {
              role: m.role as 'user' | 'assistant',
              content: m.content,
              displayContent: display,
              items: items,
              action: action,
              timestamp: new Date(m.timestamp),
              canHaveFeedback: m.role === 'assistant'
            };
          });
        } else {
          const greeting = this.getGreeting();
          this.messages.push({
            role: 'assistant',
            content: `${greeting} I've analyzed your upcoming schedule. How can I help you today?`,
            displayContent: `${greeting} I've analyzed your upcoming schedule. How can I help you today?`,
            timestamp: new Date(),
            canHaveFeedback: true
          });
        }
        this.scrollToBottom();
      },
      error: (err) => {
        console.error('Failed to load chat history:', err);
        this.messages.push({
          role: 'assistant',
          content: 'I had trouble loading your history, but I\'m here to help! What\'s on your mind?',
          displayContent: 'I had trouble loading your history, but I\'m here to help! What\'s on your mind?',
          timestamp: new Date(),
          canHaveFeedback: true
        });
      }
    });
  }

  sendMessage() {
    const text = this.userInput.trim();
    if (!text || this.isLoading) return;

    this.messages.push({
      role: 'user',
      content: text,
      timestamp: new Date()
    });
    
    this.userInput = '';
    this.isLoading = true;
    
    // Reset textarea height
    if (this.chatInput) {
      setTimeout(() => {
        this.chatInput.nativeElement.style.height = 'auto';
      }, 0);
    }

    const assistantMsg: Message = {
      role: 'assistant',
      content: '',
      timestamp: new Date(),
      canHaveFeedback: true,
      isStreaming: true
    };
    this.messages.push(assistantMsg);

    this.aiService.streamMessage(text).subscribe({
      next: (chunk) => {
        assistantMsg.content += chunk;
        
        // Try to extract message during streaming
        const match = assistantMsg.content.match(/"message"\s*:\s*"([\s\S]*?)(?:"|$)/);
        if (match) {
          assistantMsg.displayContent = match[1];
        } else {
          assistantMsg.displayContent = "Thinking...";
        }
        this.throttledScroll();
      },
      complete: () => {
        this.isLoading = false;
        assistantMsg.isStreaming = false;
        
        try {
          const cleanJson = this.extractJson(assistantMsg.content);
          const res = JSON.parse(cleanJson);
          assistantMsg.content = res.message || assistantMsg.content;
          assistantMsg.displayContent = assistantMsg.content;
          assistantMsg.items = res.items || [];
          assistantMsg.action = res.action || null;
        } catch (e) {
          if (!assistantMsg.displayContent || assistantMsg.displayContent === "Thinking...") {
            assistantMsg.displayContent = assistantMsg.content;
          }
        }
        this.refreshIcons();
        this.scrollToBottom();
      },
      error: (err) => {
        console.error('Chat stream error:', err);
        assistantMsg.content = 'Sorry, I encountered an error connecting to the reasoning layer. Please try again.';
        assistantMsg.displayContent = assistantMsg.content;
        assistantMsg.isStreaming = false;
        this.isLoading = false;
      }
    });
  }

  private throttledScroll(): void {
    const now = Date.now();
    if (now - this.lastScrollTime > 100) {
      this.scrollToBottom();
      this.lastScrollTime = now;
    }
  }

  private extractJson(text: string): string {
    const start = text.indexOf('{');
    const end = text.lastIndexOf('}');
    if (start !== -1 && end !== -1) {
      return text.substring(start, end + 1);
    }
    return text;
  }

  submitFeedback(msg: Message, isPositive: boolean) {
    if (msg.hasFeedback && msg.isPositiveFeedback === isPositive) return;

    const index = this.messages.indexOf(msg);
    let userQuery = '';
    for (let i = index - 1; i >= 0; i--) {
      if (this.messages[i].role === 'user') {
        userQuery = this.messages[i].content;
        break;
      }
    }

    this.aiService.submitFeedback(userQuery, msg.content, isPositive).subscribe({
      next: () => {
        msg.hasFeedback = true;
        msg.isPositiveFeedback = isPositive;
        this.refreshIcons();
      },
      error: (err) => console.error('Feedback failed', err)
    });
  }

  onConfirmAction(msg: Message) {
    if (!msg.action) return;
    
    this.isLoading = true;
    this.aiService.confirmAction(msg.action).subscribe({
      next: (res) => {
        msg.action = null;
        msg.content = res.message;
        msg.displayContent = res.message;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Action confirmation failed:', err);
        msg.content = 'Action failed. Please try manually.';
        msg.displayContent = msg.content;
        this.isLoading = false;
      }
    });
  }

  private scrollToBottom(): void {
    if (this.scrollContainer) {
      try {
        const el = this.scrollContainer.nativeElement;
        el.scrollTop = el.scrollHeight;
      } catch (err) {}
    }
  }

  private refreshIcons() {
    setTimeout(() => {
      if ((window as any).lucide) {
        (window as any).lucide.createIcons();
      }
    }, 50);
  }
}
