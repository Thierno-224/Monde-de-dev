describe('Articles Feed', () => {
  beforeEach(() => {
    cy.login();
    cy.visit('/articles');
  });

  it('should display available articles', () => {
    cy.get('.article-card').should('have.length', 2);

    cy.contains('.article-card', 'Introduction à Spring Boot')
      .should('be.visible');

    cy.contains('.article-card', 'Les composants Angular')
      .should('be.visible');
  });

  it('should navigate to the selected article', () => {
    cy.contains('.article-card', 'Introduction à Spring Boot')
      .click();

    cy.url().should('match', /\/articles\/\d+$/);
    cy.contains('Introduction à Spring Boot').should('be.visible');
  });

  it('should sort articles by publication date', () => {
    cy.get('.article-card')
      .first()
      .should('contain', 'Les composants Angular');

    cy.intercept('GET', '**/api/articles/feed?sort=ASC')
      .as('getArticlesAsc');

    cy.get('.sort-button').click();

    cy.wait('@getArticlesAsc')
      .its('response.statusCode')
      .should('eq', 200);

    cy.get('.article-card')
      .first()
      .should('contain', 'Introduction à Spring Boot');

    cy.get('.sort-button').should('contain', '↑');
  });
});
