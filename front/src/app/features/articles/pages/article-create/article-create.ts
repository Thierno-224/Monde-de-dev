import {Component, DestroyRef, inject} from '@angular/core';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {Header} from '../../../../shared/components/header/header';
import {Router, RouterLink} from '@angular/router';
import {ThemeService} from '../../../themes/services/theme.service';
import {Observable} from 'rxjs';
import {ThemeModel} from '../../../themes/models/theme.model';
import {AsyncPipe} from '@angular/common';
import {ArticleService} from '../../services/article.service';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-article-create',
  imports: [
    Header,
    RouterLink,
    ReactiveFormsModule,
    AsyncPipe
  ],
  templateUrl: './article-create.html',
  styleUrl: './article-create.scss',
})
export class ArticleCreate {
  private fb = inject(FormBuilder);
  private themeService = inject(ThemeService);
  private articleService = inject(ArticleService);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  articleForm = this.fb.nonNullable.group({
    themeId: [0, [Validators.required, Validators.min(1)]],
    title: ['', Validators.required],
    content: ['', Validators.required]
  });

  themes$: Observable<ThemeModel[]> = this.themeService.getAllThem();

  protected submitArticle() {
    if (this.articleForm.invalid) return;

    this.articleService.createArticle(this.articleForm.getRawValue())
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
      next: () => this.router.navigate(['/articles'])
    });
  }
}
