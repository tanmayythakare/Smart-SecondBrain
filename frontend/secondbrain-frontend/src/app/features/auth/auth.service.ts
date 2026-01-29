import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

interface LoginResponse {
  token: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private API_URL = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<LoginResponse> {
  return this.http.post<LoginResponse>(
    `${this.API_URL}/login`,
    { username, password }
  );
}



  saveToken(token: string): void {
    console.log('Saving token:', token);
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
  const token = localStorage.getItem('token');
  console.log('READING TOKEN FROM STORAGE:', token); // 👈 ADD THIS
  return token;
}


  logout(): void {
    localStorage.removeItem('token');
  }
}
