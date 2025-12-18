package com.omrbranch.pages;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.omrbranch.utility.BaseClass;

public class LoginPage extends BaseClass{
	
	public LoginPage() {
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(id = "email")
	private WebElement txtUserName;

	@FindBy(id = "pass")
	private WebElement txtPassword;

	@FindBy(xpath = "//button[@value='login']")
	private WebElement BtnLogin;
	
	@FindBy(id="errorMessage")
	private WebElement errLoginMsg;
	

	public WebElement getTxtUserName() {
		return txtUserName;
	}

	public WebElement getTxtPassword() {
		return txtPassword;
	}

	public WebElement getBtnLogin() {
		return BtnLogin;
	}

	public WebElement getErrLoginMsg() {
		return errLoginMsg;
	}

	public void Login(String userName, String password){
		elementSendKeys(txtUserName, userName);
		elementSendKeys(txtPassword,password);
		elementClick(BtnLogin);
		
	}
	
	public void LoginErrorMsg() {
		getElementText(errLoginMsg);
	}
	
	
	
}
