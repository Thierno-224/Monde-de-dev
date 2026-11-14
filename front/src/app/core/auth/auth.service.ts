import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import { environment } from '../../../environments/environment';
import {LoginRequest, LoginResponse, RegisterRequest} from './auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${environment.apiBaseUrl}/auth/login`,
      credentials
    ).pipe(
      tap(response => {
        sessionStorage.setItem('auth_token', response.token);
      })
    );
  }

  register(request: RegisterRequest) : Observable<void> {
    return this.http.post<void>(
      `${environment.apiBaseUrl}/auth/register`,
      request
    )
  }

  isAuthenticated(): boolean {
    return !!sessionStorage.getItem('auth_token');
  }

  logout(): void {
    sessionStorage.removeItem('auth_token');
  }
}
