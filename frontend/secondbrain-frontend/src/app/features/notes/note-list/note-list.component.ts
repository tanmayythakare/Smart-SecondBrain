import { Component, OnInit, ViewEncapsulation, AfterViewChecked } from '@angular/core';
import { NoteService, Note } from '../note.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-note-list',
  templateUrl: './note-list.component.html',
  styleUrls: ['./note-list.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class NoteListComponent implements OnInit, AfterViewChecked {

  ngAfterViewChecked(): void {
    if ((window as any).lucide) {
      (window as any).lucide.createIcons();
    }
  }

  notes: Note[] = [];
  
  // Form properties for create/edit
  searchQuery = '';
  noteTitle = '';
  noteContent = '';
  editingNote: Note | null = null;
  
  // Delete confirmation tracking (instead of browser confirm dialog)
  deleteConfirmNoteId: number | null = null;
  
  isLoading = false;
  isAiLoading = false;
  errorMessage = '';

  // Getter for filtered notes based on search
  get filteredNotes(): Note[] {
    const validNotes = (this.notes || []).filter(note => 
      (note?.title && note.title.trim() !== '') || (note?.content && note.content.trim() !== '')
    );

    if (!this.searchQuery.trim()) {
      return validNotes;
    }
    
    const query = this.searchQuery.toLowerCase();
    return validNotes.filter(note => 
      note.title.toLowerCase().includes(query) ||
      note.content.toLowerCase().includes(query)
    );
  }

  constructor(
    private noteService: NoteService, 
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.noteService.getNotes().subscribe(notes => {
        this.notes = notes.filter(n => n.id === Number(id));
      });
    } else {
      this.loadNotes();
    }
  }

  loadNotes() {
    this.isLoading = true;
    this.errorMessage = '';
    this.noteService.getNotes().subscribe({
      next: (notes) => {
        this.notes = notes;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load notes. Please try again.';
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  // ============================================
  // CREATE & UPDATE NOTE - NO PROMPTS
  // ============================================
  
  addOrUpdateNote() {
    if (!this.noteTitle.trim() || !this.noteContent.trim()) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    if (this.editingNote) {
      // UPDATE existing note
      this.noteService.updateNote(this.editingNote.id, this.noteTitle, this.noteContent)
        .subscribe({
          next: () => {
            this.clearNoteForm();
            this.loadNotes();
          },
          error: (err) => {
            this.errorMessage = 'Failed to update note.';
            this.isLoading = false;
            console.error(err);
          }
        });
    } else {
      // CREATE new note
      this.noteService.createNote(this.noteTitle, this.noteContent)
        .subscribe({
          next: () => {
            this.clearNoteForm();
            this.loadNotes();
          },
          error: (err) => {
            this.errorMessage = 'Failed to create note.';
            this.isLoading = false;
            console.error(err);
          }
        });
    }
  }

  // ============================================
  // EDIT NOTE - NO PROMPTS, USES FORM
  // ============================================
  
  editNote(note: Note) {
    this.router.navigate(['/notes', note.id]);
  }

  successMessage: string | null = null;

  showSuccess(message: string) {
    this.successMessage = message;
    setTimeout(() => {
      this.successMessage = null;
    }, 3000);
  }

  // Clear the form and exit edit mode
  clearNoteForm() {
    this.noteTitle = '';
    this.noteContent = '';
    this.editingNote = null;
  }

  createNewNote() {
    this.isLoading = true;
    this.noteService.createNote('New Note', 'Start writing...').subscribe({
      next: (note: any) => {
        this.isLoading = false;
        if (note && note.id) {
          this.router.navigate(['/notes', note.id]);
        } else if (note && note.content && note.content[0] && note.content[0].id) {
          // Handle Page response just in case
          this.router.navigate(['/notes', note.content[0].id]);
        } else {
          this.errorMessage = 'Failed to create note: invalid response from server.';
          console.error('createNewNote: note or note.id is undefined', note);
        }
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to create note. Please try again.';
        console.error(err);
      }
    });
  }

  // Cancel editing without saving
  cancelEdit() {
    this.clearNoteForm();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // ============================================
  // DELETE NOTE - NO PROMPTS, USES INLINE CONFIRM
  // ============================================
  
  toggleDeleteConfirm(note: Note) {
    // Instead of browser confirm(), we show an inline confirmation box
    if (this.deleteConfirmNoteId === note.id) {
      this.deleteConfirmNoteId = null; // Hide confirmation
    } else {
      this.deleteConfirmNoteId = note.id; // Show confirmation
    }
  }

  isDeleteConfirmVisible(note: Note): boolean {
    return this.deleteConfirmNoteId === note.id;
  }

  confirmDelete() {
    if (!this.deleteConfirmNoteId) return;
    this.isLoading = true;
    this.noteService.deleteNote(this.deleteConfirmNoteId)
      .subscribe({
        next: () => {
          this.showSuccess('Note deleted successfully');
          if (this.editingNote && this.editingNote.id === this.deleteConfirmNoteId) {
            this.clearNoteForm();
          }
          this.deleteConfirmNoteId = null;
          this.loadNotes();
        },
        error: (err) => {
          this.errorMessage = 'Failed to delete note.';
          this.isLoading = false;
          this.deleteConfirmNoteId = null;
          console.error(err);
        }
      });
  }

  cancelDelete(note: Note) {
    this.deleteConfirmNoteId = null;
  }

  aiAssist(instruction: string) {
    if (!this.noteContent.trim() || this.isAiLoading) return;
    
    this.isAiLoading = true;
    this.noteService.assistNote(this.noteContent, instruction).subscribe({
      next: (res) => {
        this.noteContent = res.result;
        this.isAiLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'AI Assistant failed to process the note.';
        this.isAiLoading = false;
      }
    });
  }
}