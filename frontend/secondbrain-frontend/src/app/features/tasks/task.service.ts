import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
export interface Task {
  id: number;
  title: string;
  status: 'TODO' | 'IN_PROGRESS' | 'DONE';
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  dueDate?: string;
  category?: string; // Mocked for UI
}

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  private API_URL = `${environment.apiUrl}/api/tasks`;

  constructor(private http: HttpClient) {}

  getTasks(): Observable<Task[]> {
    return this.http.get<any>(this.API_URL).pipe(
      map(response => Array.isArray(response) ? response : (response.content || []))
    );
  }

  createTask(title: string, priority: string = 'MEDIUM', dueDate: string | null = null): Observable<Task> {
    return this.http.post<Task>(this.API_URL, { title, priority, dueDate });
  }

  updateTask(id: number, title: string, status: string, priority?: string, dueDate?: string): Observable<Task> {
    const completed = status === 'DONE';
    return this.http.put<Task>(`${this.API_URL}/${id}`, { 
      title, 
      completed, 
      status, 
      priority, 
      dueDate 
    });
  }

  deleteTask(id: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/${id}`);
  }
}
