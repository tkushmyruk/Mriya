import {Observable} from 'rxjs';
import {Injectable} from '@angular/core';
import {CommentModel} from '../models/comment.model';
import {HttpClient} from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private apiUrl = 'http://localhost:8080/posts';

  constructor(private http: HttpClient) {}

  getComments(postId: string): Observable<CommentModel[]> {
    return this.http.get<CommentModel[]>(`${this.apiUrl}/${postId}/comments`);
  }

  addComment(postId: string, content: string): Observable<CommentModel> {
    return this.http.post<CommentModel>(`${this.apiUrl}/${postId}/comments`, { content });
  }
}
