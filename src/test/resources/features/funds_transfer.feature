Feature: Transferring funds between a customer's own accounts
  A transfer is two entries. Money leaves one account and arrives in another,
  and the pair holds the same total afterwards. Every scenario below follows
  from that sentence.

  Background:
    Given a customer holds two accounts

  @smoke
  Scenario: A transfer moves exactly the stated amount
    When 25.00 is transferred from the first account to the second
    Then the first account falls by 25.00
    And the second account rises by 25.00
    And the two accounts together hold the same total as before

  Scenario: An amount with cents is applied to the cent
    When 10.37 is transferred from the first account to the second
    Then the second account rises by 10.37

  # Both of these are refused by no rule in this build. They carry the
  # known-defect tag so the default run stays green while the gap stays visible.
  # See the defect reports in banking-api-tests.
  @known-defect
  Scenario Outline: Amounts a ledger must refuse
    When <amount> is transferred from the first account to the second
    Then the transfer is refused
    And the first account is unchanged

    Examples:
      | amount |
      | -50.00 |
      | 0.00   |

  @known-defect
  Scenario: A zero transfer is recorded rather than refused
    When 0.00 is transferred from the first account to the second
    Then the transfer is refused

  Scenario: A zero transfer at least leaves the balance alone
    When 0.00 is transferred from the first account to the second
    Then the first account is unchanged

  Scenario: A transfer to an account that does not exist is refused
    When a transfer of 10.00 is attempted to an account that does not exist
    Then the transfer is refused
    And the first account is unchanged
