describe('User Profile', () => {
  beforeEach(() => {
    cy.login();
    cy.visit('/profile');
  });

  it('should display user information and subscriptions', () => {
    cy.get('h1').should('contain', 'Profil utilisateur');

    cy.get('input[formControlName="username"]')
      .should('have.value', 'cypress_user');

    cy.get('input[formControlName="email"]')
      .should('have.value', 'cypress@test.com');

    cy.get('.profile__subscription-list .theme-card')
      .should('have.length', 2);

    cy.contains('.theme-card', 'Java').should('be.visible');
    cy.contains('.theme-card', 'Angular').should('be.visible');
  });

  it('should update user profile', () => {
    const username = `cypress_updated_${Date.now()}`;

    cy.get('input[formControlName="username"]')
      .clear()
      .type(username);

    cy.get('button[type="submit"]').click();

    cy.get('input[formControlName="username"]')
      .should('have.value', username);

    cy.reload();

    cy.get('input[formControlName="username"]')
      .should('have.value', username);
  });

  it('should unsubscribe from a theme', () => {
    cy.contains('.theme-card', 'Java')
      .find('button')
      .click();

    cy.contains('.profile__subscription-list .theme-card', 'Java')
      .should('not.exist');

    cy.reload();

    cy.contains('.profile__subscription-list .theme-card', 'Java')
      .should('not.exist');
  });
});
