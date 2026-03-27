import { HttpClient } from '@angular/common/http';
import {Observable} from 'rxjs';
import {ProfileModel} from '../model/profile-model';
import {Injectable} from '@angular/core';
import {UserModel} from '../../components/models/user.model';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  constructor(private readonly httpClient: HttpClient) {}

  private apiUrl = 'http://localhost:8080';

  public loadMyProfile(): Observable<ProfileModel> {
    return this.httpClient.get<ProfileModel>(`${this.apiUrl}/profile/me`);
  }

  public uploadAvatar(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.httpClient.post(`${this.apiUrl}/profile/me/upload-avatar`, formData);
  }

  public loadPublicProfile(profileId: number): Observable<ProfileModel> {
    return this.httpClient.get<ProfileModel>(`${this.apiUrl}/profile/public/${profileId}`);
  }

  public search(query: string): Observable<ProfileModel[]> {
    return this.httpClient.get<ProfileModel[]>(
      `${this.apiUrl}/profile/search`,
      { params: { query } }
    );
  }

  getFriendshipStatus(id: number | null): Observable<string> {
    return this.httpClient.get(`${this.apiUrl}/api/friends/status/${id}`, { responseType: 'text' });
  }

  sendFriendRequest(id: number): Observable<void> {
    return this.httpClient.post<void>(`${this.apiUrl}/api/friends/request/${id}`, {});
  }

  getFriendsList(): Observable<UserModel[]> {
    return this.httpClient.get<UserModel[]>(`${this.apiUrl}/api/friends/all`);
  }

  getIncomingRequests(): Observable<UserModel[]> {
    return this.httpClient.get<UserModel[]>(`${this.apiUrl}/api/friends/requests`);
  }

  acceptFriendRequest(id: number): Observable<void> {
    return this.httpClient.post<void>(`${this.apiUrl}/api/friends/accept/${id}`, {});
  }

  declineFriendRequest(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.apiUrl}/api/friends/decline/${id}`);
  }
}
