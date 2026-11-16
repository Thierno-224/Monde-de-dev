import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ThemeModel} from '../models/theme.model';
import {environment} from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})

export class ThemeService {
  private http = inject(HttpClient);

  getAllThem(): Observable<ThemeModel[]> {
    return this.http.get<ThemeModel[]>(
      `${environment.apiBaseUrl}/themes`
    )
  }
}
