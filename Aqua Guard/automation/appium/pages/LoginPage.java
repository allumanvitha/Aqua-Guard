package appium.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {
    private AndroidDriver driver;

    @FindBy(id = "com.aquaguard:id/et_email")
    private WebElement emailField;

    @FindBy(id = "com.aquaguard:id/et_password")
    private WebElement passwordField;

    @FindBy(id = "com.aquaguard:id/btn_login")
    private WebElement loginButton;

    @FindBy(id = "com.aquaguard:id/tv_forgot_password")
    private WebElement forgotPasswordButton;

    @FindBy(id = "com.aquaguard:id/tv_dont_have_account")
    private WebElement signupLink;

    public LoginPage(AndroidDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void enterEmail(String email) {
        emailField.clear();
        emailField.sendKeys(email);
    }

    public void enterPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void clickLogin() {
        loginButton.click();
    }

    public void clickForgotPassword() {
        forgotPasswordButton.click();
    }

    public void clickSignUp() {
        signupLink.click();
    }

    public boolean isLoginButtonEnabled() {
        return loginButton.isEnabled();
    }
}
