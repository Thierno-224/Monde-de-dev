import {
  AfterViewInit,
  Component,
  DestroyRef,
  ElementRef,
  inject,
  signal,
  viewChild
} from '@angular/core';

import { Header } from '../../../../shared/components/header/header';
import { ArticleService } from '../../services/article.service';
import { ArticleCard } from '../../components/article-card/article-card';
import { ArticleModel } from '../../models/article.model';
import { RouterLink } from '@angular/router';
import { finalize, Subscription } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-article-list',
  imports: [
    Header,
    ArticleCard,
    RouterLink
  ],
  templateUrl: './article-list.html',
  styleUrl: './article-list.scss',
})
export class ArticleList implements AfterViewInit {

  private readonly articleService = inject(ArticleService);
  private readonly destroyRef = inject(DestroyRef);

  readonly sort = signal<'ASC' | 'DESC'>('DESC');
  readonly articles = signal<ArticleModel[]>([]);
  readonly loading = signal(false);
  readonly last = signal(false);
  readonly error = signal(false);

  private page = 0;
  private readonly size = 10;

  private request?: Subscription;

  private readonly sentinel =
    viewChild.required<ElementRef<HTMLElement>>('sentinel');

  ngAfterViewInit(): void {
    const observer = new IntersectionObserver(
      entries => {
        if (entries[0].isIntersecting && !this.error()) {
          this.loadArticles();
        }
      },
      { rootMargin: '200px' }
    );
    observer.observe(this.sentinel().nativeElement);

    this.destroyRef.onDestroy(() => observer.disconnect());
  }

  loadArticles(): void {
    if (this.loading() || this.last()) return;

    this.loading.set(true);
    this.error.set(false);

    this.request = this.articleService
      .getFeed(this.page, this.size, this.sort())
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        finalize(() => this.loading.set(false))
      )
      .subscribe({
        next: response => {
          this.articles.update(articles => [
            ...articles,
            ...response.content
          ]);

          this.last.set(response.last);
          this.page = response.number + 1;
        },
        error: () => this.error.set(true)
      });
  }

  toggleSort(): void {
    this.request?.unsubscribe();

    this.sort.update(value =>
      value === 'DESC' ? 'ASC' : 'DESC'
    );

    this.articles.set([]);
    this.last.set(false);
    this.page = 0;

    this.loadArticles();
  }
}
