import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';

import { ArticleCreate } from './article-create';
import { environment } from '../../../../../environments/environment';

describe('ArticleCreate Integration Tests', () => {
  let fixture: ComponentFixture<ArticleCreate>;
  let component: ArticleCreate;
  let httpMock: HttpTestingController;
  let router: Router;

  const apiUrl = environment.apiBaseUrl;

  const mockThemes = [
    { id: 1, title: 'Java', description: 'Développement Java' },
    { id: 2, title: 'Angular', description: 'Développement Angular' }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ArticleCreate],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleCreate);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    httpMock.verify();
  });

  function loadThemes(): void {
    fixture.detectChanges();

    const requests = httpMock.match(
      request =>
        request.method === 'GET' &&
        request.url.includes('/themes')
    );

    expect(requests.length).toBe(1);

    requests.forEach(request => request.flush(mockThemes));

    fixture.detectChanges();
  }

  it('should display available themes in the select', () => {
    loadThemes();

    const options: HTMLOptionElement[] = Array.from(
      fixture.nativeElement.querySelectorAll('select option')
    );

    expect(options.length).toBe(3);
    expect(options[1].textContent?.trim()).toBe('Java');
    expect(options[2].textContent?.trim()).toBe('Angular');
  });

  it('should disable submit button when form is invalid', () => {
    loadThemes();

    const submitButton: HTMLButtonElement =
      fixture.nativeElement.querySelector('button[type="submit"]');

    expect(component.articleForm.invalid).toBeTrue();
    expect(submitButton.disabled).toBeTrue();

    httpMock.expectNone(
      request => request.method === 'POST'
    );
  });

  it('should create article and navigate to articles page', () => {
    loadThemes();

    const navigateSpy = spyOn(router, 'navigate')
      .and.resolveTo(true);

    component.articleForm.setValue({
      themeId: 1,
      title: 'Introduction à Spring Boot',
      content: 'Découvrir les fondamentaux de Spring Boot'
    });

    fixture.detectChanges();

    const form: HTMLFormElement =
      fixture.nativeElement.querySelector('form');

    form.dispatchEvent(
      new Event('submit', { bubbles: true, cancelable: true })
    );

    const requests = httpMock.match(
      request =>
        request.method === 'POST' &&
        request.url.includes('/articles')
    );

    expect(requests.length).toBe(1);

    const request = requests[0];

    expect(request.request.body).toEqual({
      themeId: 1,
      title: 'Introduction à Spring Boot',
      content: 'Découvrir les fondamentaux de Spring Boot'
    });

    request.flush({ id: 1 });

    expect(navigateSpy).toHaveBeenCalledOnceWith(['/articles']);
  });
});
