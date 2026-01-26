import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface Task {
  id: number;
  title: string;
}

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  private API_URL = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) {}

  getTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.API_URL);
  }

  createTask(title: string): Observable<Task> {
    return this.http.post<Task>(this.API_URL, { title });
  }
  updateTask(id: number, title: string) {
  return this.http.put<Task>(`${this.API_URL}/${id}`, { title });
}

deleteTask(id: number) {
  return this.http.delete(`${this.API_URL}/${id}`);
} 

}
