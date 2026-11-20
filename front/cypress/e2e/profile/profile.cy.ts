
describe('User Profile', () => {
  beforeEach(() => {
    cy.login();
    cy.visit('/profile');
  });
  it('should display user information and subscriptions', () => {
    cy.get('h1')
      .should('contain', 'Profil utilisateur');

    cy.get('input[formControlName="username"]')
      .should('not.have.value', '');

    cy.get('input[formControlName="email"]')
      .should('have.value', 'cypress@test.com');

    cy.get('.profile__subscription-list')
      .should('be.visible');
  });

  it('should update user profile', () => {
    const username = `cypress_${Date.now()}`;

    cy.get('input[formControlName="username"]')
      .clear()
      .type(username);

    cy.get('button[type="submit"]').click();

    cy.reload();

    cy.get('input[formControlName="username"]')
      .should('have.value', username);
  });
});
