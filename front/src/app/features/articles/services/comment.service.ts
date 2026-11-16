import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {CommentModel} from '../models/comment.model';
import {environment} from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})

export class CommentService {
  private http = inject(HttpClient);

  getArticleComment(articleId: number): Observable<CommentModel[]> {
    return this.http.get<CommentModel[]>(
      `${environment.apiBaseUrl}/articles/${articleId}/comments`
    )
  }

  addComment(articleId: number, content: string): Observable<CommentModel> {
    return this.http.post<CommentModel>(
      `${environment.apiBaseUrl}/articles/${articleId}/comments`,
      {content}
    )
  }
}
