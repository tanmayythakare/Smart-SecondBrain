import { Component, OnInit } from '@angular/core';
import { NoteService, Note } from '../note.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-note-list',
  templateUrl: './note-list.component.html'
})
export class NoteListComponent implements OnInit {

  notes: Note[] = [];
  
  // Form properties for create/edit
  searchQuery = '';
  noteTitle = '';
  noteContent = '';
  editingNote: Note | null = null;
  
  // Link modal properties
  showLinkModal = false;
  linkingNote: Note | null = null;
  linkTargetId: any = ''; // Changed to 'any' to handle both string and number
  
  // Delete confirmation tracking (instead of browser confirm dialog)
  deleteConfirmNoteId: number | null = null;
  
  backlinks: any[] = [];

  // Getter for filtered notes based on search
  get filteredNotes(): Note[] {
    if (!this.searchQuery.trim()) {
      return this.notes;
    }
    
    const query = this.searchQuery.toLowerCase();
    return this.notes.filter(note => 
      note.title.toLowerCase().includes(query) ||
      note.content.toLowerCase().includes(query)
    );
  }

  constructor(private noteService: NoteService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    const noteId = Number(id);

    if (id) {
      this.noteService.getNotes().subscribe(notes => {
        this.notes = notes.filter(n => n.id === Number(id));
        this.loadBacklinks(noteId);
      });
    } else {
      this.loadNotes();
    }
  }

  loadNotes() {
    this.noteService.getNotes().subscribe(notes => {
      this.notes = notes;
    });
  }

  loadBacklinks(noteId: number) {
    this.noteService.getBacklinks(noteId)
      .subscribe(res => this.backlinks = res);
  }

  // ============================================
  // CREATE & UPDATE NOTE - NO PROMPTS
  // ============================================
  
  addOrUpdateNote() {
    if (!this.noteTitle.trim() || !this.noteContent.trim()) {
      return;
    }

    if (this.editingNote) {
      // UPDATE existing note
      this.noteService.updateNote(this.editingNote.id, this.noteTitle, this.noteContent)
        .subscribe(() => {
          this.clearNoteForm();
          this.loadNotes();
        });
    } else {
      // CREATE new note
      this.noteService.createNote(this.noteTitle, this.noteContent)
        .subscribe(() => {
          this.clearNoteForm();
          this.loadNotes();
        });
    }
  }

  // ============================================
  // EDIT NOTE - NO PROMPTS, USES FORM
  // ============================================
  
  editNote(note: Note) {
    // Instead of using browser prompt(), we populate the form
    this.editingNote = note;
    this.noteTitle = note.title;
    this.noteContent = note.content;
    
    // Scroll to the top so user can see the form
    window.scrollTo({ top: 0, behavior: 'smooth' });
    
    // Focus the title input after a brief delay for smooth UX
    setTimeout(() => {
      const titleInput = document.querySelector('input[placeholder="Note title"]') as HTMLInputElement;
      if (titleInput) {
        titleInput.focus();
        // Optional: select all text so user can start typing immediately
        titleInput.select();
      }
    }, 300);
  }

  // Clear the form and exit edit mode
  clearNoteForm() {
    this.noteTitle = '';
    this.noteContent = '';
    this.editingNote = null;
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

  confirmDelete(note: Note) {
    this.noteService.deleteNote(note.id)
      .subscribe(() => {
        // If we were editing this note, clear the form
        if (this.editingNote && this.editingNote.id === note.id) {
          this.clearNoteForm();
        }
        this.deleteConfirmNoteId = null;
        this.loadNotes();
      });
  }

  cancelDelete(note: Note) {
    this.deleteConfirmNoteId = null;
  }

  // ============================================
  // LINK NOTES - NO PROMPTS, USES MODAL - FIXED
  // ============================================
  
  openLinkModal(note: Note) {
    // Instead of browser prompt(), we show a modal
    this.linkingNote = note;
    this.linkTargetId = '';
    this.showLinkModal = true;
  }

  closeLinkModal() {
    this.showLinkModal = false;
    this.linkingNote = null;
    this.linkTargetId = '';
  }

  linkNotes() {
    if (!this.linkingNote || !this.linkTargetId) {
      return;
    }

    // Convert to number and validate
    const targetId = Number(this.linkTargetId);
    
    // Check if it's a valid number
    if (isNaN(targetId) || targetId <= 0) {
      console.error('Invalid note ID');
      return;
    }

    this.noteService.linkNotes(this.linkingNote.id, targetId)
      .subscribe(() => {
        this.closeLinkModal();
        this.loadNotes();
      }, error => {
        console.error('Link error:', error);
        // You could show an error message in the modal instead of alert
      });
  }

  // ============================================
  // VIEW RELATED NOTES
  // ============================================
  
  viewRelated(note: any) {
    this.noteService.getRelatedNotes(note.id)
      .subscribe(related => {
        console.log('RELATED NOTES:', related);
        // You can display this in a modal or side panel instead of alert
      });
  }
}