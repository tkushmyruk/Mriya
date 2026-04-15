import { HttpClient } from '@angular/common/http';
import {inject, Injectable, PLATFORM_ID} from '@angular/core';
import { Observable, tap } from 'rxjs';
import {isPlatformBrowser} from '@angular/common';
import {PresenceService} from './presense.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private platformId = inject(PLATFORM_ID);
  private apiUrl = 'http://localhost:8080/users/';

  constructor(private http: HttpClient,
              private presenceService: PresenceService) {}

  isLoggedIn(): boolean {
    if (isPlatformBrowser(this.platformId)) {
      return !!localStorage.getItem('access_token');
    }
    return false;
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


  private handleAuthSuccess(res: any) {
    if (res.token) {
      localStorage.setItem('access_token', res.token);
      localStorage.setItem('user_id', res.userId.toString());
      this.presenceService.startPresenceHeartbeat(res.userId).subscribe();
    }
  }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}auth/authenticate`, credentials).pipe(
      tap(res => this.handleAuthSuccess(res))
    );
  }

  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}auth/register`, userData);
  }

  verifyCode(code: string): Observable<any> {
    return this.http.post(`${this.apiUrl}auth/verify`, code).pipe(
      tap(res => this.handleAuthSuccess(res))
    );
  }
}



