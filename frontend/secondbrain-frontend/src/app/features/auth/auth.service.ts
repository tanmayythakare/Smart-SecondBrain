import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

interface LoginResponse {
  token: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private API_URL = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<LoginResponse> {
  return this.http.post<LoginResponse>(
    `${this.API_URL}/login`,
    { username, password }
  );
}
  saveToken(token: string): void {
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
  const token = localStorage.getItem('token');
  return token;
}


  logout(): void {
    localStorage.removeItem('token');
  }
}
