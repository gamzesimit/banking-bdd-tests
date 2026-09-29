package com.qa.bdd.steps;

import com.qa.bdd.support.Bank;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferSteps {

    private final Bank bank = new Bank();

    private BigDecimal firstBefore;
    private BigDecimal secondBefore;
    private Response lastResponse;

    @Given("a customer holds two accounts")
    public void aCustomerHoldsTwoAccounts() {
        firstBefore = bank.balanceOf(Bank.FIRST_ACCOUNT);
        secondBefore = bank.balanceOf(Bank.SECOND_ACCOUNT);
        assertThat(firstBefore).as("the first account must be readable").isNotNull();
        assertThat(secondBefore).as("the second account must be readable").isNotNull();
    }

    @When("{word} is transferred from the first account to the second")
    public void isTransferred(String amount) {
        lastResponse = bank.transfer(Bank.FIRST_ACCOUNT, Bank.SECOND_ACCOUNT, amount);
    }

    @When("a transfer of {word} is attempted to an account that does not exist")
    public void transferToMissingAccount(String amount) {
        lastResponse = bank.transfer(Bank.FIRST_ACCOUNT, Bank.MISSING_ACCOUNT, amount);
    }

    @Then("the first account falls by {word}")
    public void firstFallsBy(String amount) {
        BigDecimal after = bank.balanceOf(Bank.FIRST_ACCOUNT);
        assertThat(firstBefore.subtract(after))
                .as("the paying account must fall by exactly the amount")
                .usingComparator(BigDecimal::compareTo)
                .isEqualTo(new BigDecimal(amount));
    }

    @Then("the second account rises by {word}")
    public void secondRisesBy(String amount) {
        BigDecimal after = bank.balanceOf(Bank.SECOND_ACCOUNT);
        assertThat(after.subtract(secondBefore))
                .as("the receiving account must rise by exactly the amount")
                .usingComparator(BigDecimal::compareTo)
                .isEqualTo(new BigDecimal(amount));
    }

    @Then("the two accounts together hold the same total as before")
    public void theTotalIsUnchanged() {
        BigDecimal after = bank.balanceOf(Bank.FIRST_ACCOUNT).add(bank.balanceOf(Bank.SECOND_ACCOUNT));
        assertThat(after)
                .as("a transfer creates and destroys nothing")
                .usingComparator(BigDecimal::compareTo)
                .isEqualTo(firstBefore.add(secondBefore));
    }

    @Then("the transfer is refused")
    public void theTransferIsRefused() {
        assertThat(lastResponse.statusCode())
                .as("the API answered %s", lastResponse.statusCode())
                .isGreaterThanOrEqualTo(400);
    }

    @Then("the first account is unchanged")
    public void theFirstAccountIsUnchanged() {
        assertThat(bank.balanceOf(Bank.FIRST_ACCOUNT))
                .as("a refused transfer must leave the balance where it was")
                .usingComparator(BigDecimal::compareTo)
                .isEqualTo(firstBefore);
    }
}
