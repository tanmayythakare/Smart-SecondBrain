import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
export interface Task {
  id: number;
  title: string;
}

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  private API_URL = `${environment.apiUrl}/tasks`;

  constructor(private http: HttpClient) {}
  private getAuthHeaders(): HttpHeaders {
  const token = localStorage.getItem('token');
  if (token) {
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }
  return new HttpHeaders();
}

// Then update ALL methods to use it:
getTasks(): Observable<Task[]> {
  return this.http.get<Task[]>(this.API_URL, { headers: this.getAuthHeaders() });
}

  createTask(title: string): Observable<Task> {
  // Add headers here!
  return this.http.post<Task>(this.API_URL, { title }, { headers: this.getAuthHeaders() });
}

updateTask(id: number, title: string) {
  // Add headers here!
  return this.http.put<Task>(`${this.API_URL}/${id}`, { title }, { headers: this.getAuthHeaders() });
}

deleteTask(id: number) {
  // Add headers here!
  return this.http.delete(`${this.API_URL}/${id}`, { headers: this.getAuthHeaders() });
}

}
