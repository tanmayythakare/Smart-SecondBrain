import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NoteService, Note } from '../note.service';

@Component({
  selector: 'app-note-detail',
  templateUrl: './note-detail.component.html',
  styleUrls: ['./note-detail.component.css']
})
export class NoteDetailComponent implements OnInit {

  note: Note | null = null;
  assistantQuery = '';
  isAiLoading = false;
  aiResult = '';
  successMessage: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private noteService: NoteService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadNote(Number(id));
    }
    this.refreshIcons();
  }

  private refreshIcons() {
    setTimeout(() => {
      if ((window as any).lucide) {
        (window as any).lucide.createIcons();
      }
    }, 100);
  }

  loadNote(id: number) {
    this.noteService.getNoteById(id).subscribe({
      next: (note) => {
        this.note = note;
      },
      error: (err) => {
        console.error('Failed to load note', err);
        this.router.navigate(['/notes']);
      }
    });
  }

  onTitleBlur(event: any) {
    if (this.note) {
      this.note.title = event.target.innerText;
    }
  }

  onContentInput(event: any) {
    if (this.note) {
      this.note.content = event.target.innerText;
    }
  }

  saveNote() {
    if (!this.note) return;
    this.noteService.updateNote(this.note.id, this.note.title, this.note.content).subscribe({
      next: () => {
        this.successMessage = 'Changes saved successfully';
        setTimeout(() => this.successMessage = null, 3000);
      }
    });
  }

  isDeleting = false;
  showDeleteConfirm = false;

  deleteNote() {
    this.showDeleteConfirm = true;
  }

  cancelDelete() {
    this.showDeleteConfirm = false;
  }

  confirmDelete() {
    if (!this.note) return;
    this.isDeleting = true;
    this.noteService.deleteNote(this.note.id).subscribe({
      next: () => {
        this.router.navigate(['/notes']);
      },
      error: (err) => {
        console.error('Failed to delete note', err);
        this.isDeleting = false;
        this.showDeleteConfirm = false;
      }
    });
  }

  aiAssist(instruction: string) {
    if (!this.note || this.isAiLoading) return;
    this.isAiLoading = true;
    this.noteService.assistNote(this.note.content, instruction).subscribe({
      next: (res) => {
        this.aiResult = res.result;
        this.isAiLoading = false;
      },
      error: () => {
        this.isAiLoading = false;
      }
    });
  }

  applyAiResult() {
    if (this.note && this.aiResult) {
      this.note.content = this.aiResult;
      this.aiResult = '';
      this.saveNote();
    }
  }

  askAssistant() {
    if (!this.assistantQuery.trim()) return;
    this.aiAssist(this.assistantQuery);
    this.assistantQuery = '';
  }
}
