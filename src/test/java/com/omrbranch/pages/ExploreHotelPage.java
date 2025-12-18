package com.omrbranch.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.omrbranch.utility.BaseClass;

public class ExploreHotelPage extends BaseClass{

	public ExploreHotelPage() {
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath = "//a[@data-testid='username']")
	private WebElement loginSuccessMsg;

	public WebElement getLoginSuccessMsg() {
		return loginSuccessMsg;
	}
	
	
	public String LoginSuccess() {
		String afterloginMsg = getElementText(loginSuccessMsg);
		return afterloginMsg;
	
	}

}
