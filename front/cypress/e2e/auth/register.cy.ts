
describe('User Registration', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it('should reject invalid registration data', () => {
    cy.get('#username').type('testuser');
    cy.get('#email').type('invalid-email').blur();
    cy.get('#password').type('123').blur();

    cy.contains('Veuillez saisir une adresse e-mail valide.')
      .should('be.visible');

    cy.contains('Le mot de passe ne respecte pas les critères requis.')
      .should('be.visible');

    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('should reject an already registered email', () => {
    cy.get('#username').type('another_user');
    cy.get('#email').type('cypress@test.com');
    cy.get('#password').type('Test@123', { log: false });

    cy.get('button[type="submit"]').click();

    cy.get('[role="alert"]').should('be.visible');
    cy.url().should('include', '/register');
  });

  it('should reject an already registered username', () => {
    cy.get('#username').type('cypress_user');
    cy.get('#email').type('another@test.com');
    cy.get('#password').type('Test@123', { log: false });

    cy.get('button[type="submit"]').click();

    cy.get('[role="alert"]').should('be.visible');
    cy.url().should('include', '/register');
  });

  it('should register a new user successfully', () => {
    const uniqueId = Date.now();

    cy.get('#username').type(`user_${uniqueId}`);
    cy.get('#email').type(`user_${uniqueId}@test.com`);
    cy.get('#password').type('Test@123', { log: false });

    cy.get('button[type="submit"]').click();

    cy.url().should('include', '/login');
  });
});
