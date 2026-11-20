import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { Profile } from './profile';
import {environment} from '../../../../../environments/environment';

describe('Profile Integration Tests', () => {
  let component: Profile;
  let fixture: ComponentFixture<Profile>;
  let httpMock: HttpTestingController;

  const apiUrl = environment.apiBaseUrl;

  const mockUser = {
    id: 1,
    username: 'testuser',
    email: 'test@example.com'
  };

  const mockThemes = [
    {
      id: 1,
      title: 'Java',
      description: 'Développement Java'
    },
    {
      id: 2,
      title: 'Angular',
      description: 'Développement Angular'
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Profile],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Profile);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  function loadProfile(): void {
    fixture.detectChanges();

    const profileRequest = httpMock.expectOne(
      `${apiUrl}/auth/me`
    );

    expect(profileRequest.request.method).toBe('GET');
    profileRequest.flush(mockUser);

    const themeRequests = httpMock.match(
      request => request.url.includes('subscriptions')
    );

    themeRequests.forEach(request => {
      expect(request.request.method).toBe('GET');
      request.flush(mockThemes);
    });

    fixture.detectChanges();
  }

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load user profile into the form', () => {
    loadProfile();

    expect(component.userForm.value.username).toBe('testuser');
    expect(component.userForm.value.email).toBe('test@example.com');
  });

  it('should load user profile into the form', () => {
    loadProfile();

    expect(component.userForm.value.username).toBe('testuser');
    expect(component.userForm.value.email).toBe('test@example.com');

    const usernameInput: HTMLInputElement =
      fixture.nativeElement.querySelector(
        'input[formControlName="username"]'
      );

    const emailInput: HTMLInputElement =
      fixture.nativeElement.querySelector(
        'input[formControlName="email"]'
      );

    expect(usernameInput.value).toBe('testuser');
    expect(emailInput.value).toBe('test@example.com');
  });

  it('should update user profile and reset password', () => {
    loadProfile();

    component.userForm.patchValue({
      username: 'newusername',
      email: 'new@example.com',
      password: 'password123'
    });

    component.saveProfile();

    const request = httpMock.expectOne(`${apiUrl}/users/me`);

    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual({
      username: 'newusername',
      email: 'new@example.com',
      password: 'password123'
    });

    request.flush({
      ...mockUser,
      username: 'newusername',
      email: 'new@example.com'
    });

    expect(component.userForm.controls.password.value).toBe('');
  });

  it('should not update profile when form is invalid', () => {
    loadProfile();

    component.userForm.patchValue({
      username: '',
      email: 'invalid-email'
    });

    expect(component.userForm.invalid).toBeTrue();

    component.saveProfile();

    httpMock.expectNone(`${apiUrl}/users/me`);
  });
});
