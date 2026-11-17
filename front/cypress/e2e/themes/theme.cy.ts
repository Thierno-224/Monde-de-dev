describe('Theme Subscriptions', () => {
  beforeEach(() => {
    cy.login();
    cy.visit('/themes');
  });

  it('should display available themes', () => {
    cy.get('.theme-card').should('have.length', 3);

    cy.contains('.theme-card', 'Java').should('be.visible');
    cy.contains('.theme-card', 'Angular').should('be.visible');
    cy.contains('.theme-card', 'DevOps').should('be.visible');
  });

  it('should show an existing subscription', () => {
    cy.contains('.theme-card', 'Java').within(() => {
      cy.contains('button', 'Déjà abonné')
        .should('be.disabled');
    });
  });
/*
  it('should allow subscribing to a theme', () => {
    cy.contains('.theme-card', 'DevOps').within(() => {
      cy.contains('button', "S'abonner").click();

      cy.contains('button', 'Déjà abonné')
        .should('be.disabled');
    });
  });

 */
});
