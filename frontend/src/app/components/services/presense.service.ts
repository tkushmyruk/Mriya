import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {Observable, interval, of, timer, distinctUntilChanged} from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import {AuthService} from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class PresenceService {
  private globalHeartbeat?: any;
  private readonly baseUrl = 'http://localhost:8080/users';

  constructor(private http: HttpClient) {}

  isUserOnline(userId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/${userId}/online`).pipe(
      catchError(() => of(false))
    );
  }

  getLiveStatus(userId: number): Observable<boolean> {
    return timer(0, 30000).pipe(
      switchMap(() => this.isUserOnline(userId)),
      distinctUntilChanged(),
      catchError(() => of(false))
    );
  }

  updateMyStatus(userId: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/me/heartbeat?userId=${userId}`, {});
  }

  startPresenceHeartbeat(userId: number) {
    return timer(0, 120000).pipe(
      switchMap(() => this.updateMyStatus(userId)),
      catchError(err => {
        console.error('Presence heartbeat failed', err);
        return of(null);
      })
    );
  }
}
