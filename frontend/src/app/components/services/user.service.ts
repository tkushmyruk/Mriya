import {HttpClient} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  getCurrentUser(): Observable<any> {
    return this.http.get(`${this.apiUrl}/profile/me`);
  }

  updateProfile(data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/profile/update`, data);
  }

  getUserNameByUserId(userId: number): Observable<string> {
    return this.http.get(`${this.apiUrl}/profile/public/user/${userId}`, { responseType: 'text' }).pipe(
      catchError(error => {
        console.error(`Помилка завантаження користувача ${userId}:`, error);
        return throwError(() => new Error('Не вдалося завантажити дані користувача'));
      })
    );
  }
}
