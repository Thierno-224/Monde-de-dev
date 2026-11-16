import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ArticleModel} from '../models/article.model';
import {environment} from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})

export class ArticleService {
  private http = inject(HttpClient);

  getMyFeed(sort: 'ASC' | 'DESC' = 'DESC'): Observable<ArticleModel[]> {
    return this.http.get<ArticleModel[]>(
      `${environment.apiBaseUrl}/articles/feed?sort=${sort}`
    );
  }

  getArticleById(id: number): Observable<ArticleModel> {
    return this.http.get<ArticleModel>(
      `${environment.apiBaseUrl}/articles/${id}`
    )
  }
}
