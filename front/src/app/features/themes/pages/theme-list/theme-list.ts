import {ChangeDetectorRef, Component, DestroyRef, inject} from '@angular/core';
import {Header} from '../../../../shared/components/header/header';
import {Observable} from 'rxjs';
import {ThemeModel} from '../../models/theme.model';
import {ThemeService} from '../../services/theme.service';
import {AsyncPipe} from '@angular/common';
import {ThemeCard} from '../../component/theme-card/theme-card';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-theme-list',
  imports: [
    Header,
    AsyncPipe,
    ThemeCard
  ],
  templateUrl: './theme-list.html',
  styleUrl: './theme-list.scss',
})
export class ThemeList {
  private themeService = inject(ThemeService);
  private destroyRef = inject(DestroyRef);
  private cdr = inject(ChangeDetectorRef);

  themes$: Observable<ThemeModel[]> = this.themeService.getAllThem();

  protected onSubscribe(themeId: number) {
    this.themeService.subscribe(themeId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.themes$ = this.themeService.getAllThem();
          this.cdr.markForCheck();
        }
      })
  }
}
