import {Component, DestroyRef, inject, input, InputSignal} from '@angular/core';
import {Header} from '../../../../shared/components/header/header';
import {UserService} from '../../services/user-service';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {ThemeCard} from '../../../themes/component/theme-card/theme-card';
import {ThemeModel} from '../../../themes/models/theme.model';
import {map, Observable} from 'rxjs';
import {ThemeService} from '../../../themes/services/theme.service';
import {AsyncPipe} from '@angular/common';
import {UpdateUserModel} from '../../models/user-model';

@Component({
  selector: 'app-profile',
  imports: [
    Header,
    ReactiveFormsModule,
    ThemeCard,
    AsyncPipe
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile {
  private userService = inject(UserService);
  private fb = inject(FormBuilder);
  private destroyRef = inject(DestroyRef);
  private themeService = inject(ThemeService);

  theme: InputSignal<ThemeModel> = input.required<ThemeModel>();
  subscribedThemes$: Observable<ThemeModel[]> = this.themeService.getAllThem()
    .pipe(
      map(themes => themes.filter(sub => sub.subscribed)),
    )

  userForm = this.fb.nonNullable.group({
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['']
  })

  saveProfile(): void {
    if (this.userForm.invalid) return;

    const { username, email, password } = this.userForm.getRawValue();

    const request: UpdateUserModel = {
      username,
      email,
      password: password.trim() || null
    };

    this.userService.updateProfile(request)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.userForm.controls.password.reset();
        },
        error: error => {
          console.error('Erreur de modification du profil', error);
        }
      });
  }

  ngOnInit(): void {
    this.userService.getMyProfile()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: user => {
          this.userForm.patchValue({
            username: user.username,
            email: user.email
          })
        }
      })
  }
}
