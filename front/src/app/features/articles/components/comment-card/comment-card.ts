import {Component, input, InputSignal} from '@angular/core';
import {CommentModel} from '../../models/comment.model';

@Component({
  selector: 'app-comment-card',
  imports: [],
  templateUrl: './comment-card.html',
  styleUrl: './comment-card.scss',
})
export class CommentCard {
  comment: InputSignal<CommentModel> = input.required<CommentModel>();
}
