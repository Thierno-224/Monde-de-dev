import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  TestRequest,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { ArticleList } from './article-list';
import { environment } from '../../../../../environments/environment';

describe('ArticleList Integration Tests', () => {
  let fixture: ComponentFixture<ArticleList>;
  let component: ArticleList;
  let httpMock: HttpTestingController;

  const feedUrl = `${environment.apiBaseUrl}/articles/feed`;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ArticleList],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleList);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  function expectFeedRequest(sort: 'ASC' | 'DESC'): TestRequest {
    const request = httpMock.expectOne(
      `${feedUrl}?page=0&size=10&sort=${sort}`
    );

    expect(request.request.method).toBe('GET');

    return request;
  }

  function emptyPage() {
    return {
      content: [],
      totalPages: 0,
      totalElements: 0,
      numberOfElements: 0,
      number: 0,
      size: 10,
      first: true,
      last: true,
      empty: true
    };
  }

  it('should load articles with DESC sorting by default', async () => {
    fixture.detectChanges();
    await fixture.whenStable();

    component.loadArticles();

    expectFeedRequest('DESC').flush(emptyPage());

    fixture.detectChanges();

    expect(component.sort()).toBe('DESC');
  });

  it('should display empty message when no articles exist', async () => {
    fixture.detectChanges();
    await fixture.whenStable();

    component.loadArticles();

    expectFeedRequest('DESC').flush(emptyPage());

    fixture.detectChanges();

    const content: HTMLElement = fixture.nativeElement;

    expect(content.textContent)
      .toContain('Aucun article disponible.');
  });

  it('should request ASC sorting after clicking sort button', async () => {
    fixture.detectChanges();
    await fixture.whenStable();

    component.loadArticles();

    expectFeedRequest('DESC').flush(emptyPage());

    fixture.detectChanges();

    const sortButton: HTMLButtonElement =
      fixture.nativeElement.querySelector('.sort-button');

    sortButton.click();

    fixture.detectChanges();

    expect(component.sort()).toBe('ASC');

    expectFeedRequest('ASC').flush(emptyPage());

    fixture.detectChanges();

    expect(sortButton.textContent).toContain('↑');
  });
});
