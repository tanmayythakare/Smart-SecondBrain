import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
export interface Note {
  id: number;
  title: string;
  content: string;
  createdAt?: Date;
  updatedAt?: Date;
  linkedNotes?: number[];
}

@Injectable({
  providedIn: 'root'
})
export class NoteService {

  private API_URL = `${environment.apiUrl}/notes`;

  constructor(private http: HttpClient) { }

  // Helper method to get auth headers (if you're using authentication)
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token'); // or sessionStorage, or your auth service
    if (token) {
      return new HttpHeaders({
        'Authorization': `Bearer ${token}`
      });
    }
    return new HttpHeaders();
  }

  // Get all notes
  getNotes(): Observable<Note[]> {
    return this.http.get<Note[]>(this.API_URL, { headers: this.getAuthHeaders() });
  }

  // Get note by ID
  getNoteById(id: number): Observable<Note> {
    return this.http.get<Note>(`${this.API_URL}/${id}`, { headers: this.getAuthHeaders() });
  }

  // Create new note
  createNote(title: string, content: string): Observable<Note> {
    return this.http.post<Note>(this.API_URL, { title, content }, { headers: this.getAuthHeaders() });
  }

  // Update existing note
  updateNote(id: number, title: string, content: string): Observable<Note> {
    return this.http.put<Note>(`${this.API_URL}/${id}`, { title, content }, { headers: this.getAuthHeaders() });
  }

  // Delete note - FIXED
  deleteNote(id: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/${id}`, { headers: this.getAuthHeaders() });
  }

  // Search notes
  searchNotes(q: string): Observable<Note[]> {
    return this.http.get<Note[]>(
      `${this.API_URL}/search?q=${encodeURIComponent(q)}`,
      { headers: this.getAuthHeaders() }
    );
  }

  // Link two notes together
  linkNotes(sourceId: number, targetId: number): Observable<any> {
    return this.http.post(
      `http://localhost:8080/api/note-links?sourceId=${sourceId}&targetId=${targetId}`,
      {},
      { headers: this.getAuthHeaders() }
    );
  }

  // Get related notes (linked notes)
  getRelatedNotes(noteId: number): Observable<any[]> {
    return this.http.get<any[]>(
      `http://localhost:8080/api/note-links/${noteId}`,
      { headers: this.getAuthHeaders() }
    );
  }

  // Get backlinks (notes that link to this note)
  getBacklinks(noteId: number): Observable<any[]> {
    return this.http.get<any[]>(
      `http://localhost:8080/api/note-links/backlinks/${noteId}`,
      { headers: this.getAuthHeaders() }
    );
  }
}