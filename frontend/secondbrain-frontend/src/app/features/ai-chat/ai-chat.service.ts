import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from '../auth/auth.service';

export interface LinkedItem {
  id: number;
  type: 'task' | 'note';
  title: string;
  dueDate?: string;
}

export interface ChatResponse {
  message: string;
  items?: LinkedItem[];
}

@Injectable({
  providedIn: 'root'
})
export class AiChatService {
  private apiUrl = `${environment.apiUrl}/api/ai/chat`;

  constructor(private http: HttpClient, private authService: AuthService) {}

  getHistory(): Observable<any[]> {
    const historyUrl = `${environment.apiUrl}/api/ai/chat/history`;
    return this.http.get<any[]>(historyUrl);
  }

  sendMessage(message: string): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(this.apiUrl, { message });
  }

  streamMessage(message: string): Observable<string> {
    const streamUrl = `${environment.apiUrl}/api/ai/chat/stream`;
    
    return new Observable(observer => {
      fetch(streamUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'text/event-stream',
          'Authorization': `Bearer ${this.authService.getToken()}`
        },
        body: JSON.stringify({ message })
      }).then(response => {
        if (!response.ok) {
          observer.error(`HTTP Error: ${response.status}`);
          return;
        }

        const reader = response.body?.getReader();
        const decoder = new TextDecoder();
        let buffer = '';
        
        const push = () => {
          reader?.read().then(({ done, value }) => {
            if (done) {
              if (buffer.trim()) {
                this.processLine(buffer, observer);
              }
              observer.complete();
              return;
            }

            buffer += decoder.decode(value, { stream: true });
            
            // SSE chunks are separated by double newlines \n\n
            const chunks = buffer.split('\n\n');
            buffer = chunks.pop() || '';

            for (const chunk of chunks) {
              // Each chunk might contain multiple data: lines
              const lines = chunk.split('\n');
              for (const line of lines) {
                this.processLine(line, observer);
              }
            }
            push();
          }).catch(err => observer.error(err));
        };
        push();
      }).catch(err => observer.error(err));
    });
  }

  private processLine(line: string, observer: any) {
    const trimmed = line.trim();
    if (!trimmed) return;
    
    if (trimmed.startsWith('data:')) {
      const data = trimmed.substring(5).trim();
      if (data === '[DONE]') {
        observer.complete();
      } else if (data) {
        observer.next(data);
      }
    }
  }

  submitFeedback(userQuery: string, assistantMessage: string, isPositive: boolean): Observable<any> {
    const feedbackUrl = `${environment.apiUrl}/api/ai/feedback`;
    return this.http.post(feedbackUrl, { userQuery, assistantMessage, isPositive });
  }

  confirmAction(action: any): Observable<ChatResponse> {
    const confirmUrl = `${environment.apiUrl}/api/ai/chat/confirm`;
    return this.http.post<ChatResponse>(confirmUrl, action);
  }

  getSidebarData(): Observable<any> {
    const sidebarUrl = `${environment.apiUrl}/api/ai/context/sidebar`;
    return this.http.get<any>(sidebarUrl);
  }
}
