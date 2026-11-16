import {Component, inject} from '@angular/core';
import {Header} from '../../../../shared/components/header/header';
import {Observable} from 'rxjs';
import {ThemeModel} from '../../models/theme.model';
import {ThemeService} from '../../services/theme.service';
import {AsyncPipe} from '@angular/common';
import {ThemeCard} from '../../component/theme-card/theme-card';

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

  themes$: Observable<ThemeModel[]> = this.themeService.getAllThem();
}
