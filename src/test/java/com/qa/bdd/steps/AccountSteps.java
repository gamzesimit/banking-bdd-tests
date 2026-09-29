package com.qa.bdd.steps;

import com.qa.bdd.support.Bank;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountSteps {

    private final Bank bank = new Bank();
    private Response response;

    @When("the seeded account is read")
    public void theSeededAccountIsRead() {
        response = bank.account(Bank.FIRST_ACCOUNT);
    }

    @When("an account that does not exist is read")
    public void aMissingAccountIsRead() {
        response = bank.account(Bank.MISSING_ACCOUNT);
    }

    @When("the accounts of the seeded customer are listed")
    public void theAccountsAreListed() {
        response = bank.accountsOf(Bank.SEEDED_CUSTOMER_ID);
    }

    @Then("the response carries an identifier, a type and a balance")
    public void theResponseCarriesTheExpectedShape() {
        response.then().statusCode(200);
        assertThat((Object) response.jsonPath().get("id")).as("an account needs an identifier").isNotNull();
        assertThat(response.jsonPath().getString("type")).as("an account needs a type").isNotBlank();
        assertThat((Object) response.jsonPath().get("balance")).as("an account needs a balance").isNotNull();
    }

    @Then("the balance carries no more than two decimal places")
    public void theBalanceCarriesNoMoreThanCents() {
        // The API returns money as a JSON number, so a balance of 100.00
        // arrives as 100.0 and one of 100 arrives as 100. What can be asserted
        // is that no balance carries a fraction smaller than a cent, which is
        // the rule that actually matters for a ledger.
        String balance = response.jsonPath().get("balance").toString();
        assertThat(balance)
                .as("a balance must not carry a fraction smaller than a cent")
                .matches("-?\\d+(\\.\\d{1,2})?");
    }

    @Then("every account in the list belongs to that customer")
    public void everyAccountBelongsToThatCustomer() {
        List<Integer> owners = response.then().statusCode(200).extract().jsonPath().getList("customerId", Integer.class);
        assertThat(owners).as("the customer must hold at least one account").isNotEmpty();
        assertThat(owners).as("no account may belong to another customer").containsOnly(Bank.SEEDED_CUSTOMER_ID);
    }

    @Then("the response is not a success")
    public void theResponseIsNotASuccess() {
        assertThat(response.statusCode())
                .as("an unknown account must not answer 200")
                .isNotEqualTo(200);
    }
}
