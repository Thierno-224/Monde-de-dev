import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';

import { Register } from './register';
import { AuthService } from '../../../../core/auth/auth.service';
import { environment } from '../../../../../environments/environment';

describe('Register Integration Tests', () => {
  let fixture: ComponentFixture<Register>;
  let component: Register;
  let httpMock: HttpTestingController;
  let router: Router;

  const registerUrl = `${environment.apiBaseUrl}/auth/register`;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Register],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);

    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  function fillForm(): void {
    const username: HTMLInputElement =
      fixture.nativeElement.querySelector('#username');

    const email: HTMLInputElement =
      fixture.nativeElement.querySelector('#email');

    const password: HTMLInputElement =
      fixture.nativeElement.querySelector('#password');

    username.value = 'testuser';
    username.dispatchEvent(new Event('input'));

    email.value = 'test@example.com';
    email.dispatchEvent(new Event('input'));

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

  it('should register successfully and navigate to login', () => {
    const navigateSpy = spyOn(router, 'navigate')
      .and.resolveTo(true);

    fillForm();
    submitForm();

    const request = httpMock.expectOne(registerUrl);

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      username: 'testuser',
      email: 'test@example.com',
      password: 'password123'
    });

    request.flush(null);

    expect(navigateSpy).toHaveBeenCalledOnceWith(['/login']);
    expect(component.isLoading()).toBeFalse();
  });

  it('should prevent registration when form is invalid', () => {
    const button: HTMLButtonElement =
      fixture.nativeElement.querySelector('button[type="submit"]');

    expect(component.registerForm.invalid).toBeTrue();
    expect(button.disabled).toBeTrue();

    submitForm();

    httpMock.expectNone(registerUrl);
  });

  it('should display error message when username or email already exists', () => {
    fillForm();
    submitForm();

    const request = httpMock.expectOne(registerUrl);

    request.flush(
      { message: 'Conflict' },
      { status: 409, statusText: 'Conflict' }
    );

    fixture.detectChanges();

    const errorElement: HTMLElement =
      fixture.nativeElement.querySelector('[role="alert"]');

    const button: HTMLButtonElement =
      fixture.nativeElement.querySelector('button[type="submit"]');

    expect(errorElement.textContent?.trim())
      .toBe('Ce nom d’utilisateur ou cette adresse e-mail est déjà utilisé.');

    expect(component.isLoading()).toBeFalse();
    expect(button.disabled).toBeFalse();
  });
});
