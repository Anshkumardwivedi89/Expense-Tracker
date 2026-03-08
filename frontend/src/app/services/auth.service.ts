import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private tokenKey = 'expense_token';

  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<string> {
    return this.http.post(`${environment.apiUrl}/auth/login`, { username, password }, {
      responseType: 'text'
    });
  }

  register(username: string, email: string, password: string): Observable<string> {
    return this.http.post(`${environment.apiUrl}/auth/register`, { username, email, password }, {
      responseType: 'text'
    });
  }

  setToken(token: string) {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  loggedIn(): boolean {
    return !!this.getToken();
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
  }
}
