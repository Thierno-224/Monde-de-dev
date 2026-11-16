import {Component, DestroyRef, inject, input, signal} from '@angular/core';
import {ArticleService} from '../../services/article.service';
import {takeUntilDestroyed, toObservable} from '@angular/core/rxjs-interop';
import {combineLatest, switchMap} from 'rxjs';
import {AsyncPipe, DatePipe} from '@angular/common';
import {RouterLink} from '@angular/router';
import {Header} from '../../../../shared/components/header/header';
import {CommentService} from '../../services/comment.service';
import {CommentCard} from '../../components/comment-card/comment-card';
import {FormControl, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';

@Component({
  selector: 'app-article-detail',
  imports: [
    AsyncPipe,
    RouterLink,
    Header,
    DatePipe,
    CommentCard,
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './article-detail.html',
  styleUrl: './article-detail.scss',
})
export class ArticleDetail {
    private articleService = inject(ArticleService);
    private commentService = inject(CommentService);
  private destroyRef = inject(DestroyRef);

    id = input.required<string>();
    refreshComments = signal(0);

    article$ = toObservable(this.id)
      .pipe(
        switchMap(id => this.articleService.getArticleById(Number(id)))
      );

  comments$ = combineLatest([
    toObservable(this.id),
    toObservable(this.refreshComments)
  ]).pipe(
    switchMap(([id]) =>
      this.commentService.getArticleComment(Number(id))
    )
  );

  commentControl = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required]
  });

  submitComment(): void {
    const content = this.commentControl.value.trim();

    if (!content) return;

    this.commentService.addComment(Number(this.id()), content)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.commentControl.reset();
        this.refreshComments.update(value => value + 1);
      });
  }
}
