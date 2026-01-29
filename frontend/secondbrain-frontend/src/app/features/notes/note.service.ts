import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Note {
  id: number;
  title: string;
  content: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class NoteService {

  private API_URL = 'http://localhost:8080/api/notes';

  constructor(private http: HttpClient) {}
  

  getNotes(): Observable<Note[]> {
    return this.http.get<Note[]>(this.API_URL);
  }
  updateNote(id: number, title: string, content: string) {
  return this.http.put<Note>(`${this.API_URL}/${id}`, { title, content });
}

deleteNote(id: number) {
  return this.http.delete(`${this.API_URL}/${id}`);
}


  createNote(title: string, content: string): Observable<Note> {
    return this.http.post<Note>(this.API_URL, { title, content });
  }
  linkNotes(sourceId: number, targetId: number) {
  return this.http.post(
    `http://localhost:8080/api/note-links?sourceId=${sourceId}&targetId=${targetId}`,
    {}
  );
}

getRelatedNotes(noteId: number) {
  return this.http.get<any[]>(
    `http://localhost:8080/api/note-links/${noteId}`
  );
}
getNoteById(id: number) {
  return this.http.get<Note[]>(`http://localhost:8080/api/notes`);
}
searchNotes(q: string) {
  return this.http.get<Note[]>(
    `http://localhost:8080/api/notes/search?q=${encodeURIComponent(q)}`
  );
}
getBacklinks(noteId: number) {
  return this.http.get<any[]>(
    `http://localhost:8080/api/note-links/backlinks/${noteId}`
  );
}

}





