package selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {
    private WebDriver driver;

    @FindBy(id = "email")
    private WebElement emailInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "login-btn")
    private WebElement loginButton;

    @FindBy(id = "forgot-password-link")
    private WebElement forgotPasswordLink;

    @FindBy(id = "error-message")
    private WebElement errorMessage;

    @FindBy(id = "success-message")
    private WebElement successMessage;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void enterEmail(String email) {
        emailInput.clear();
        emailInput.sendKeys(email);
    }

    public void enterPassword(String password) {
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    public void clickLogin() {
        loginButton.click();
    }

    public void clickForgotPassword() {
        forgotPasswordLink.click();
    }

    public String getErrorMessage() {
        return errorMessage.getText();
    }

    public String getSuccessMessage() {
        return successMessage.getText();
    }

    public boolean isLoginBtnEnabled() {
        return loginButton.isEnabled();
    }
}
