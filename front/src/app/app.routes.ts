import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

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
    path: 'articles',
    title: 'Articles | MDD',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/articles/pages/article-list/article-list').then(m => m.ArticleList)
  },
  {
    path: 'articles/create',
    title: 'Créer un article | MDD',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/articles/pages/article-create/article-create').then(m => m.ArticleCreate)
  },
  {
    path: 'articles/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/articles/pages/article-detail/article-detail').then(m => m.ArticleDetail)
  },
  {
    path: 'themes',
    title: 'Themes | MDD',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/themes/pages/theme-list/theme-list').then(m => m.ThemeList)
  },
  {
    path: 'profile',
    title: 'Profil | MDD',
    canActivate: [authGuard],
    loadComponent:() => import('./features/profile/pages/profile/profile')
      .then(m => m.Profile)
  },
  {
    path: '',
    title: 'Accueil | MDD',
    loadComponent: () =>
      import('./features/home/pages/welcome/welcome').then(m => m.Welcome)
  },
];
