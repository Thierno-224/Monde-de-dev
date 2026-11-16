import {Component, input, InputSignal} from '@angular/core';
import {ArticleModel} from '../../models/article.model';
import {DatePipe} from '@angular/common';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-article-card',
  imports: [
    DatePipe,
    RouterLink
  ],
  templateUrl: './article-card.html',
  styleUrl: './article-card.scss',
})
export class ArticleCard {

  article: InputSignal<ArticleModel> = input.required<ArticleModel>()
}
