import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Post } from '../models/post.model';

@Injectable({
  providedIn: 'root'
})
export class FeedService {
  private apiUrl = 'http://localhost:8080/posts';

  constructor(private http: HttpClient) {}

  getSmartFeed(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/feed`);
  }
}
