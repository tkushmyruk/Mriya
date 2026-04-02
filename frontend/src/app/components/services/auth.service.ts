import { HttpClient } from '@angular/common/http';
import {inject, Injectable, PLATFORM_ID} from '@angular/core';
import { Observable, tap } from 'rxjs';
import {isPlatformBrowser} from '@angular/common';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private platformId = inject(PLATFORM_ID);
  private apiUrl = 'http://localhost:8080/users/';

  constructor(private http: HttpClient) {}

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}auth/authenticate`, credentials).pipe(
      tap((res: any) => {
        if (res.token) {
          localStorage.setItem('access_token', res.token);
          localStorage.setItem('user_id', res.userId.toString());

          console.log("Auth: Logged in as Profile ID:", res.profileId);
        }
      })
    );
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('access_token');
      localStorage.removeItem('user_id');
    }
  }

  getUserId(): number | null {
    if (isPlatformBrowser(this.platformId)) {
      const id = localStorage.getItem('user_id');
      return id ? Number(id) : null;
    }
    return null;
  }

  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}auth/register`, userData);
  }
}
