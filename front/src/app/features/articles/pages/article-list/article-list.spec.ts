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
      `${feedUrl}?sort=${sort}`
    );

    expect(request.request.method).toBe('GET');

    return request;
  }

  it('should load articles with DESC sorting by default', async () => {
    fixture.detectChanges();
    await fixture.whenStable();

    const request = expectFeedRequest('DESC');

    request.flush([]);

    fixture.detectChanges();

    expect(component.sort()).toBe('DESC');
  });

  it('should display empty message when no articles exist', async () => {
    fixture.detectChanges();
    await fixture.whenStable();

    expectFeedRequest('DESC').flush([]);

    fixture.detectChanges();

    const content: HTMLElement = fixture.nativeElement;

    expect(content.textContent)
      .toContain('Aucun article disponible.');
  });

  it('should request ASC sorting after clicking sort button', async () => {
    fixture.detectChanges();
    await fixture.whenStable();

    expectFeedRequest('DESC').flush([]);

    fixture.detectChanges();

    const sortButton: HTMLButtonElement =
      fixture.nativeElement.querySelector('.sort-button');

    sortButton.click();

    fixture.detectChanges();
    await fixture.whenStable();

    expect(component.sort()).toBe('ASC');

    expectFeedRequest('ASC').flush([]);

    fixture.detectChanges();

    expect(sortButton.textContent).toContain('↑');
  });
});
