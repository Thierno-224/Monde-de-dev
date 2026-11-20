import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';

import { Login } from './login';
import { AuthService } from '../../../../core/auth/auth.service';
import { environment } from '../../../../../environments/environment';

describe('Login Integration Tests', () => {
  let fixture: ComponentFixture<Login>;
  let component: Login;
  let httpMock: HttpTestingController;
  let authService: AuthService;
  let router: Router;

  const loginUrl = `${environment.apiBaseUrl}/auth/login`;

  beforeEach(async () => {
    sessionStorage.removeItem('auth_token');

    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;

    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);

    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
    sessionStorage.removeItem('auth_token');
  });

  function fillForm(): void {
    const identifier: HTMLInputElement =
      fixture.nativeElement.querySelector('#identifier');

    const password: HTMLInputElement =
      fixture.nativeElement.querySelector('#password');

    identifier.value = 'test@example.com';
    identifier.dispatchEvent(new Event('input'));

    password.value = 'password123';
    password.dispatchEvent(new Event('input'));

    fixture.detectChanges();
  }

  function submitForm(): void {
    const form: HTMLFormElement =
      fixture.nativeElement.querySelector('form');

    form.dispatchEvent(
      new Event('submit', { bubbles: true, cancelable: true })
    );
  }

  it('should login, store token and navigate to articles', () => {
    const navigateSpy = spyOn(router, 'navigate')
      .and.resolveTo(true);

    fillForm();
    submitForm();

    const request = httpMock.expectOne(loginUrl);

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      identifier: 'test@example.com',
      password: 'password123'
    });

    request.flush({ token: 'fake-jwt-token' });

    expect(authService.getToken()).toBe('fake-jwt-token');
    expect(navigateSpy).toHaveBeenCalledOnceWith(['/articles']);
    expect(component.isLoading()).toBeFalse();
  });

  it('should prevent login when form is invalid', () => {
    const button: HTMLButtonElement =
      fixture.nativeElement.querySelector('button[type="submit"]');

    expect(component.loginForm.invalid).toBeTrue();
    expect(button.disabled).toBeTrue();

    submitForm();

    httpMock.expectNone(loginUrl);
    expect(authService.isAuthenticated()).toBeFalse();
  });

  it('should display error message when credentials are incorrect', () => {
    fillForm();
    submitForm();

    const request = httpMock.expectOne(loginUrl);

    request.flush(
      { message: 'Unauthorized' },
      { status: 401, statusText: 'Unauthorized' }
    );

    fixture.detectChanges();

    const errorElement: HTMLElement =
      fixture.nativeElement.querySelector('[role="alert"]');

    const button: HTMLButtonElement =
      fixture.nativeElement.querySelector('button[type="submit"]');

    expect(errorElement.textContent?.trim())
      .toBe('Identifiant ou mot de passe incorrect.');

    expect(component.isLoading()).toBeFalse();
    expect(button.disabled).toBeFalse();
    expect(authService.getToken()).toBeNull();
  });
});
