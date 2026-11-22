import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommentCard } from './comment-card';
import { CommentModel } from '../../models/comment.model';

describe('CommentCard', () => {
  let component: CommentCard;
  let fixture: ComponentFixture<CommentCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommentCard]
    }).compileComponents();

    fixture = TestBed.createComponent(CommentCard);
    component = fixture.componentInstance;

    const mockComment: CommentModel = {
      id: 1,
      content: 'Très bon article !',
      author: 'Test User',
      createdAt: '2026-11-22T09:00:00'
    };

    fixture.componentRef.setInput('comment', mockComment);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
