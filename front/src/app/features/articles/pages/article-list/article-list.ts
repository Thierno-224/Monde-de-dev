import {Component, inject, signal} from '@angular/core';
import {Header} from '../../../../shared/components/header/header';
import {ArticleService} from '../../services/article.service';
import {Observable, switchMap} from 'rxjs';
import {ArticleModel} from '../../models/article.model';
import {AsyncPipe, JsonPipe} from '@angular/common';
import {ArticleCard} from '../../components/article-card/article-card';
import {RouterLink} from '@angular/router';
import {toObservable} from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-article-list',
  imports: [
    Header,
    AsyncPipe,
    JsonPipe,
    ArticleCard,
    RouterLink
  ],
  templateUrl: './article-list.html',
  styleUrl: './article-list.scss',
})
export class ArticleList {
  private articleService = inject(ArticleService);

  sort = signal<'ASC' | 'DESC'>('DESC');

  articles$ = toObservable(this.sort).pipe(
    switchMap(sort => this.articleService.getMyFeed(sort))
  );

  toggleSort(): void {
    this.sort.update(value => value === 'DESC' ? 'ASC' : 'DESC');
  }
}
