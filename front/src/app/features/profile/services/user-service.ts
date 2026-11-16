import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {UpdateUserModel, UserModel} from '../models/user-model';
import {environment} from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})

export class UserService {
  private http = inject(HttpClient);

  getMyProfile() : Observable<UserModel> {
    return this.http.get<UserModel>(
      `${environment.apiBaseUrl}/auth/me`
    )
  }

  updateProfile(request: UpdateUserModel): Observable<UserModel> {
    return this.http.put<UserModel>(
      `${environment.apiBaseUrl}/users/me`,
      request
    );
  }
}
