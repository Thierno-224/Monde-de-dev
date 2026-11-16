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
    path: 'articles',
    title: 'Articles | MDD',
    loadComponent: () =>
      import('./features/articles/pages/article-list/article-list').then(m => m.ArticleList)
  },
  {
    path: 'articles/:id',
    loadComponent: () =>
      import('./features/articles/pages/article-detail/article-detail').then(m => m.ArticleDetail)
  },
  {
    path: 'themes',
    title: 'Themes | MDD',
    loadComponent: () =>
      import('./features/themes/pages/theme-list/theme-list').then(m => m.ThemeList)
  },
  {
    path: '',
    title: 'Accueil | MDD',
    loadComponent: () =>
      import('./features/home/pages/welcome/welcome').then(m => m.Welcome)
  },
];
