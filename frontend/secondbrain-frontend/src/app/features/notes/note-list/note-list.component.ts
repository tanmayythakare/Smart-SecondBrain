import { Component, OnInit } from '@angular/core';
import { NoteService, Note } from '../note.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-note-list',
  templateUrl: './note-list.component.html'
})
export class NoteListComponent implements OnInit {

  notes: Note[] = [];
  title = '';
  content = '';
  search = '';

onSearch() {
  if (!this.search.trim()) {
    this.loadNotes();
    return;
  }
  this.noteService.searchNotes(this.search)
    .subscribe(res => this.notes = res);
}
backlinks: any[] = [];

loadBacklinks(noteId: number) {
  this.noteService.getBacklinks(noteId)
    .subscribe(res => this.backlinks = res);
}


  constructor(private noteService: NoteService,private route: ActivatedRoute) {}

  ngOnInit(): void {
  const id = this.route.snapshot.paramMap.get('id');
  const noteId=Number(id);

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

  addNote() {
    if (!this.title.trim() || !this.content.trim()) return;

    this.noteService.createNote(this.title, this.content)
      .subscribe(() => {
        this.title = '';
        this.content = '';
        this.loadNotes();
      });
  }
  editNote(note: Note) {
  const newTitle = prompt('Edit title', note.title);
  const newContent = prompt('Edit content', note.content);

  if (!newTitle || !newContent) return;

  this.noteService.updateNote(note.id, newTitle, newContent)
    .subscribe(() => this.loadNotes());
}

deleteNote(note: Note) {
  if (!confirm('Delete this note?')) return;

  this.noteService.deleteNote(note.id)
    .subscribe(() => this.loadNotes());
}
linkNote(note: any) {
  const targetId = prompt('Enter ID of note to link with');
  if (!targetId) return;

  this.noteService.linkNotes(note.id, Number(targetId))
    .subscribe(() => {
      alert('Notes linked');
    });
}

viewRelated(note: any) {
  this.noteService.getRelatedNotes(note.id)
    .subscribe(related => {
      console.log('RELATED NOTES:', related);
      alert(
        related.length
          ? related.map((n: any) => n.title).join(', ')
          : 'No related notes'
      );
    });
}


}
