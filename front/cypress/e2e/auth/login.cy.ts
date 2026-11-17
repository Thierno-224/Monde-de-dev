describe('User Login', () => {
  beforeEach(() => {
    cy.visit('/login');
  });

  it('should display the login form', () => {
    cy.get('#identifier').should('be.visible');
    cy.get('#password').should('be.visible');
    cy.get('button[type="submit"]').should('be.visible');
  });

  it('should disable the submit button when the form is empty', () => {
    cy.get('button[type="submit"]').should('be.disabled');
  });

  it('should display validation errors for required fields', () => {
    cy.get('#identifier').focus().blur();
    cy.get('#password').focus().blur();

    cy.contains("L'identifiant est obligatoire.").should('be.visible');
    cy.contains('Le mot de passe est obligatoire.').should('be.visible');
  });

  it('should reject invalid credentials', () => {
    cy.get('#identifier').type('wrong@example.com');
    cy.get('#password').type('WrongPassword123!', { log: false });

    cy.get('button[type="submit"]').click();

    cy.get('[role="alert"]').should('be.visible');
    cy.url().should('include', '/login');
  });

  it('should log in with valid credentials', () => {
    cy.get('#identifier').type('cypress@test.com');
    cy.get('#password').type('Test@123', { log: false });

    cy.get('button[type="submit"]').click();

    cy.url().should('include', '/articles');
  });
});
