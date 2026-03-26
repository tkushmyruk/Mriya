import { HttpClient } from '@angular/common/http';
import {Observable} from 'rxjs';
import {ProfileModel} from '../model/profile-model';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  constructor(private readonly httpClient: HttpClient) {}

  public load(userId: number): Observable<ProfileModel> {
    return this.httpClient.get<ProfileModel>(
      `http://localhost:8080/profile/${userId}`
    );
  }

  uploadAvatar(file: File, userId: number): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.httpClient.post(`http://localhost:8080/profile/${userId}/upload-avatar`, formData);
  }
}
