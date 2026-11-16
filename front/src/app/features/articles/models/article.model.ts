export interface ArticleModel {
  id: number;
  title: string;
  content: string;
  author: string;
  theme: string;
  createdAt: Date
}

export interface CreateArticleModel {
  title: string;
  content: string;
  themeId: number;
}
