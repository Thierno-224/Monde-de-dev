import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ArticleModel, CreateArticleModel} from '../models/article.model';
import {environment} from '../../../../environments/environment';
import {Page} from '../../../shared/models/page.model';

@Injectable({
  providedIn: 'root'
})

export class ArticleService {
  private http = inject(HttpClient);

  getFeed(
    page: number = 0,
    size: number = 10,
    sort: 'ASC' | 'DESC' = 'DESC'
  ): Observable<Page<ArticleModel>> {

    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', sort);

    return this.http.get<Page<ArticleModel>>(
      `${environment.apiBaseUrl}/articles/feed`,
      { params }
    );
  }

  getArticleById(id: number): Observable<ArticleModel> {
    return this.http.get<ArticleModel>(
      `${environment.apiBaseUrl}/articles/${id}`
    )
  }

  createArticle(request: CreateArticleModel): Observable<ArticleModel> {
    return this.http.post<ArticleModel>(
      `${environment.apiBaseUrl}/articles`,
      request
    )
  }
}
