package com.omrbranch.stepdefinition;

import com.omrbranch.manager.PageObjectManager;

import io.cucumber.java.en.*;
import junit.framework.Assert;

public class TC001_LoginStep {
	private final PageObjectManager pom = new PageObjectManager();

	@Given("User is on the OMR Branch hotel page")
	public void userIsOnTheOMRBranchHotelPage() {
	}

	@When("User enters {string} and {string}")
	public void userEntersAnd(String userName, String password) {
		pom.getLoginPage().Login(userName, password);
	}

	@Then("User should verify success message after login {string}")
	public void userShouldVerifySuccessMessageAfterLogin(String expectedSuccessMessage) {
		String loginSuccessMsg = pom.getExploreHotelPage().LoginSuccess();
		Assert.assertEquals("Verify Login Message", "Welcome Prakash", loginSuccessMsg);
	}

	@When("User enters {string} and {string} with enter key")
	public void userEntersAndWithEnterKey(String userName, String password) {
	}

	@Then("User should verify error message after login {string}")
	public void userShouldVerifyErrorMessageAfterLogin(String expectedErrorMessage) {
	}

}

