describe('Article Creation', () => {
  beforeEach(() => {
    cy.login();
    cy.visit('/articles/create');
  });

  it('should prevent submitting an incomplete article', () => {
    cy.get('button[type="submit"]').should('be.disabled');

    cy.get('input[formControlName="title"]')
      .type('My new article');

    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('should create an article successfully', () => {
    const title = `Cypress Article ${Date.now()}`;

    cy.get('select[formControlName="themeId"]')
      .select('Java');

    cy.get('input[formControlName="title"]')
      .type(title);

    cy.get('textarea[formControlName="content"]')
      .type('This article was created by Cypress E2E tests.');

    cy.intercept('POST', '**/api/articles').as('createArticle');

    cy.get('button[type="submit"]').click();

    cy.wait('@createArticle')
      .its('response.statusCode')
      .should('be.oneOf', [200, 201]);

    cy.url().should('include', '/articles');

    cy.contains('.article-card', title)
      .should('be.visible');
  });
});
