describe('Article Details and Comments', () => {
  beforeEach(() => {
    cy.login();
    cy.visit('/articles');

    cy.contains('.article-card', 'Introduction à Spring Boot')
      .click();

    cy.get('.article-detail h1')
      .should('contain', 'Introduction à Spring Boot');
  });

  it('should display article details and existing comments', () => {
    cy.get('.article-detail .article-content')
      .should('contain', 'Découvrez les fondamentaux de Spring Boot.');

    cy.get('.comment-card')
      .its('length')
      .should('be.at.least', 1);

    cy.contains(
      '.comment-content',
      'Excellent article pour découvrir Spring Boot !'
    ).should('be.visible');
  });

  it('should publish a new comment', () => {
    const comment = `Cypress comment ${Date.now()}`;

    cy.get('.send-button').should('be.disabled');

    cy.get('.comment-form textarea')
      .type(comment);

    cy.intercept('POST', '**/api/articles/*/comments')
      .as('createComment');

    cy.get('.send-button').click();

    cy.wait('@createComment')
      .its('response.statusCode')
      .should('eq', 201);

    cy.contains('.comment-content', comment)
      .should('be.visible');
  });
});
