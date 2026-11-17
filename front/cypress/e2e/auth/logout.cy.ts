describe('Logout', () => {
  beforeEach(() => {
    cy.login();
    cy.visit('/profile');
  });

  it('should log out the user and prevent access to protected pages', () => {
    cy.contains('button', 'Se déconnecter').click();

    cy.url().should('include', '/login');

    cy.visit('/profile');

    cy.url().should('include', '/login');
  });
});
