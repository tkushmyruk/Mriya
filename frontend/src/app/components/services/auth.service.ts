import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8080/users/';

  constructor(private http: HttpClient) {}

  login(credentials: any): Observable<any> {
    console.log("AuthService: Attempting login with credentials", credentials);
    return this.http.post(`${this.apiUrl}auth/authenticate`, credentials).pipe(
      tap((res: any) => {
        console.log("AuthService: Received response from login", res);
        if (res.token) {
          console.log("AuthService: Storing access token in localStorage", res.token);
          localStorage.setItem('access_token', res.token);
        }
      })
    );
  }

  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}auth/register`, userData);
  }
}
