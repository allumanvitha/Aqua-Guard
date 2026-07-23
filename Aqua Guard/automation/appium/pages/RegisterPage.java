package appium.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class RegisterPage {
    private AndroidDriver driver;

    @FindBy(id = "com.aquaguard:id/et_name")
    private WebElement nameField;

    @FindBy(id = "com.aquaguard:id/et_email")
    private WebElement emailField;

    @FindBy(id = "com.aquaguard:id/et_password")
    private WebElement passwordField;

    @FindBy(id = "com.aquaguard:id/btn_register")
    private WebElement registerButton;

    @FindBy(id = "com.aquaguard:id/tv_already_have_account")
    private WebElement loginLink;

    public RegisterPage(AndroidDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void enterName(String name) {
        nameField.clear();
        nameField.sendKeys(name);
    }

    public void enterEmail(String email) {
        emailField.clear();
        emailField.sendKeys(email);
    }

    public void enterPassword(String password) {
        passwordField.clear();
        passwordField.sendKeys(password);
    }

    public void clickRegister() {
        registerButton.click();
    }

    public void clickLoginLink() {
        loginLink.click();
    }
}
