import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { ThemeList } from './theme-list';
import {provideRouter} from '@angular/router';

describe('ThemeList', () => {
  let component: ThemeList;
  let fixture: ComponentFixture<ThemeList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ],
      imports: [ThemeList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ThemeList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
