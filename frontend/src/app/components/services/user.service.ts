import {HttpClient} from '@angular/common/http';
import {catchError, Observable, throwError} from 'rxjs';
import {Injectable} from '@angular/core';
import {ProfileModel} from '../../profile/model/profile-model';

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

  getProfileByUserId(userId: number): Observable<ProfileModel> {
    return this.http.get<ProfileModel>(`${this.apiUrl}/profile/public/user/${userId}`).pipe(
    );
  }
}
