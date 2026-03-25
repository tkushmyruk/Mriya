import { HttpClient } from '@angular/common/http';
import {Observable} from 'rxjs';
import {ProfileModel} from '../model/profile-model';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root' // Цей рядок каже Angular: "зроби цей сервіс доступним всюди"
})
export class ProfileService {
  constructor(private readonly httpClient: HttpClient) {}

  public load(): Observable<ProfileModel> {
    return this.httpClient.get<ProfileModel>(
      `http://localhost:8080/profile/8`
    );
  }

  uploadAvatar(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.httpClient.post(`http://localhost:8080/profile/1/upload-avatar`, formData);
  }
}
