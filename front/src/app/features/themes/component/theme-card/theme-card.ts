import {Component, Input, input, InputSignal, output} from '@angular/core';
import {ThemeModel} from '../../models/theme.model';

@Component({
  selector: 'app-theme-card',
  imports: [],
  templateUrl: './theme-card.html',
  styleUrl: './theme-card.scss',
})
export class ThemeCard {
  theme: InputSignal<ThemeModel> = input.required<ThemeModel>()

  themeClicked = output<number>();

  mode = input<'list' | 'profile'>('list');
}
