import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
export interface Note {
  id: number;
  title: string;
  content: string;
  createdAt?: Date;
  updatedAt?: Date;
}

@Injectable({
  providedIn: 'root'
})
export class NoteService {

  private API_URL = `${environment.apiUrl}/api/notes`;

  constructor(private http: HttpClient) { }

  // Get all notes
  getNotes(): Observable<Note[]> {
    return this.http.get<any>(this.API_URL).pipe(
      map(res => {
        if (Array.isArray(res)) return res;
        if (res && res.content && Array.isArray(res.content)) return res.content;
        return [];
      })
    );
  }

  // Get note by ID
  getNoteById(id: number): Observable<Note> {
    return this.http.get<Note>(`${this.API_URL}/${id}`);
  }

  // Create new note
  createNote(title: string, content: string): Observable<Note> {
    return this.http.post<Note>(this.API_URL, { title, content });
  }

  // Update existing note
  updateNote(id: number, title: string, content: string): Observable<Note> {
    return this.http.put<Note>(`${this.API_URL}/${id}`, { title, content });
  }

  // Delete note - FIXED
  deleteNote(id: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/${id}`);
  }

  // Search notes
  searchNotes(q: string): Observable<Note[]> {
    return this.http.get<any>(
      `${this.API_URL}/search?q=${encodeURIComponent(q)}`
    ).pipe(
      map(res => {
        if (Array.isArray(res)) return res;
        if (res && res.content && Array.isArray(res.content)) return res.content;
        return [];
      })
    );
  }

  // AI Assist
  assistNote(content: string, instruction: string): Observable<{result: string}> {
    return this.http.post<{result: string}>(`${environment.apiUrl}/api/ai/notes/assist`, { content, instruction });
  }

}