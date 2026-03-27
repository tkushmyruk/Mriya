import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Post, OwnerType } from '../models/post.model';

@Injectable({ providedIn: 'root' })
export class PostService {
  private apiUrl = 'http://localhost:8080/posts';

  constructor(private http: HttpClient) {}

  toggleLike(postId: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${postId}/like`, {});
  }

  getPosts(ownerId: number, ownerType: OwnerType): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/${ownerType}/${ownerId}`);
  }

  createPost(post: Partial<Post>): Observable<Post> {
    return this.http.post<Post>(this.apiUrl, post);
  }
}
