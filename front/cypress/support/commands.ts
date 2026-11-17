
Cypress.Commands.add('login', () => {
  cy.visit('/login');

  cy.get('#identifier').type('cypress@test.com');
  cy.get('#password').type('Test@123', { log: false });

  cy.get('button[type="submit"]').click();

  cy.url().should('include', '/articles');
});

declare global {
  namespace Cypress {
    interface Chainable {
      login(): Chainable<void>;
    }
  }
}

export {};
