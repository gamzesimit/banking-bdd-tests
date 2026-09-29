# Banking BDD tests

[![tests](https://github.com/gamzesimit/banking-bdd-tests/actions/workflows/tests.yml/badge.svg)](https://github.com/gamzesimit/banking-bdd-tests/actions/workflows/tests.yml)

Cucumber scenarios for a retail banking API, written in Gherkin so the rule is
readable by someone who does not read Java, and backed by REST Assured.

## Running it

```bash
docker compose up -d
mvn test
```

The known defects are excluded from the default run:

```bash
mvn test -Dcucumber.filter.tags="@known-defect"
```

## What is here

```
src/test/resources/features/   the scenarios, in Gherkin
src/test/java/com/qa/bdd/steps/  the step definitions
src/test/java/com/qa/bdd/support/Bank.java  the one place that talks to the bank
```

Ten scenarios. Three carry `@known-defect` and are excluded from the default
run, so the suite stays green while the gap stays visible.

## Why the feature file is written this way

A feature file earns its place when a person who does not read code can check
whether the rule is right. That means the scenario states the rule, not the
click path:

```gherkin
Scenario: A transfer moves exactly the stated amount
  When 25.00 is transferred from the first account to the second
  Then the first account falls by 25.00
  And the second account rises by 25.00
  And the two accounts together hold the same total as before
```

The last line is the one that matters and the one a click based scenario always
misses. A transfer that debits and does not credit passes every other check.

## Known defects

Three scenarios are tagged `@known-defect`: a negative transfer and a zero
transfer are both accepted with a 200. The negative case is reported in
[banking-api-tests](https://github.com/gamzesimit/banking-api-tests) as PB-005,
where it is also shown to reverse the direction of the money. The zero case
leaves the balance alone, so it costs nothing today, but an empty transaction
in the ledger is still a row somebody has to explain.
