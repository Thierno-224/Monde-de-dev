describe('Articles Feed', () => {
  beforeEach(() => {
    cy.login();
    cy.visit('/articles');
  });

  it('should display available articles', () => {
    cy.get('.article-card')
      .should('have.length.at.least', 1);
  });

  it('should navigate to the selected article', () => {
    cy.get('.article-card').first().click();

    cy.url().should('match', /\/articles\/\d+$/);
  });

  it('should sort articles by publication date', () => {
    cy.get('.article-card')
      .should('have.length.at.least', 1);

    cy.get('.sort-button').click();

    cy.get('.sort-button').should('contain', '↑');

    cy.get('.article-card')
      .should('have.length.at.least', 1);
  });
});
