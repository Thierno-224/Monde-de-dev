import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { ArticleCard } from './article-card';
import { ArticleModel } from '../../models/article.model';

describe('ArticleCard', () => {
  let component: ArticleCard;
  let fixture: ComponentFixture<ArticleCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ArticleCard],
      providers: [provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleCard);
    component = fixture.componentInstance;

    const mockArticle = {
      id: 1,
      title: 'Introduction à Spring Boot',
      content: 'Découvrir Spring Boot',
      createdAt: '2026-11-22T09:00:00'
    };

    fixture.componentRef.setInput('article', mockArticle);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
