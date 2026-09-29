Feature: Reading an account
  The figures a customer disputes a charge from have to be complete and
  belong to the account they were read from.

  @smoke
  Scenario: An account carries the fields a statement needs
    When the seeded account is read
    Then the response carries an identifier, a type and a balance
    And the balance carries two decimal places

  Scenario: Every account in a customer's list belongs to that customer
    When the accounts of the seeded customer are listed
    Then every account in the list belongs to that customer

  Scenario: An account that does not exist is not reported as success
    When an account that does not exist is read
    Then the response is not a success
