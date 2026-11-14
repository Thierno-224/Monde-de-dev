import { Routes } from '@angular/router';

export const routes: Routes = [{
  path: 'login',
  title: 'Connexion | MDD',
  loadComponent: () =>
    import('./features/auth/pages/login/login').then(m => m.Login)
},
  {
    path: 'register',
    title: 'Inscription | MDD',
    loadComponent: () =>
      import('./features/auth/pages/register/register').then(m => m.Register)
  },
  {
    path: '',
    title: 'Accueil | MDD',
    loadComponent: () =>
      import('./features/home/pages/welcome/welcome').then(m => m.Welcome)
  },
];
