package appium.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import appium.pages.LoginPage;
import appium.pages.RegisterPage;
import selenium.utils.TestMetadata;

public class AuthAppiumTests extends BaseTest {

    @Test
    @TestMetadata(testId = "TC_APP_001", module = "Launch", feature = "App Cold Launch Successful", priority = "High")
    public void testAppLaunch() {
        LoginPage login = new LoginPage(driver);
        Assert.assertNotNull(login);
    }

    @Test
    @TestMetadata(testId = "TC_APP_002", module = "Login", feature = "Login Valid Credentials Flow", priority = "High")
    public void testLoginValidCredentials() {
        LoginPage login = new LoginPage(driver);
        login.enterEmail("test@aquaguard.com");
        login.enterPassword("password123");
        login.clickLogin();
    }

    @Test
    @TestMetadata(testId = "TC_APP_003", module = "Login", feature = "Login Invalid Email Validation", priority = "High")
    public void testLoginInvalidEmail() {
        LoginPage login = new LoginPage(driver);
        login.enterEmail("invalid_email");
        login.enterPassword("password");
        Assert.assertFalse(login.isLoginButtonEnabled());
    }

    @Test
    @TestMetadata(testId = "TC_APP_004", module = "Login", feature = "Login Blank Email field", priority = "High")
    public void testLoginBlankEmail() {
        LoginPage login = new LoginPage(driver);
        login.enterEmail("");
        login.enterPassword("password123");
        Assert.assertFalse(login.isLoginButtonEnabled());
    }

    @Test
    @TestMetadata(testId = "TC_APP_005", module = "Login", feature = "Login Blank Password field", priority = "High")
    public void testLoginBlankPassword() {
        LoginPage login = new LoginPage(driver);
        login.enterEmail("test@aquaguard.com");
        login.enterPassword("");
        Assert.assertFalse(login.isLoginButtonEnabled());
    }

    @Test
    @TestMetadata(testId = "TC_APP_006", module = "Register", feature = "Register Valid Credentials Flow", priority = "High")
    public void testRegisterValidCredentials() {
        LoginPage login = new LoginPage(driver);
        login.clickSignUp();
        RegisterPage reg = new RegisterPage(driver);
        reg.enterName("Manikanta");
        reg.enterEmail("newuser@aquaguard.com");
        reg.enterPassword("Pass@123");
        reg.clickRegister();
    }

    @Test
    @TestMetadata(testId = "TC_APP_007", module = "Register", feature = "Register Empty Fields Validation", priority = "Medium")
    public void testRegisterEmptyFields() {
        LoginPage login = new LoginPage(driver);
        login.clickSignUp();
        RegisterPage reg = new RegisterPage(driver);
        reg.enterName("");
        reg.clickRegister();
    }

    @Test
    @TestMetadata(testId = "TC_APP_008", module = "Register", feature = "Register Existing User validation", priority = "High")
    public void testRegisterExistingUser() {
        LoginPage login = new LoginPage(driver);
        login.clickSignUp();
        RegisterPage reg = new RegisterPage(driver);
        reg.enterName("Manikanta");
        reg.enterEmail("test@aquaguard.com");
        reg.enterPassword("password123");
        reg.clickRegister();
    }

    @Test
    @TestMetadata(testId = "TC_APP_009", module = "Forgot Password", feature = "Forgot Password valid email", priority = "High")
    public void testForgotPasswordValidEmail() {
        LoginPage login = new LoginPage(driver);
        login.clickForgotPassword();
    }

    @Test
    @TestMetadata(testId = "TC_APP_010", module = "Forgot Password", feature = "Forgot Password empty email", priority = "Medium")
    public void testForgotPasswordEmptyEmail() {
        LoginPage login = new LoginPage(driver);
        login.clickForgotPassword();
    }

    @Test
    @TestMetadata(testId = "TC_APP_011", module = "Login", feature = "Remember Me Checkbox functionality", priority = "Low")
    public void testRememberMeCheckbox() {
        LoginPage login = new LoginPage(driver);
    }

    @Test
    @TestMetadata(testId = "TC_APP_012", module = "Login", feature = "Google Sign In Option presence", priority = "Medium")
    public void testGoogleSignInOption() {
        LoginPage login = new LoginPage(driver);
    }

    @Test
    @TestMetadata(testId = "TC_APP_013", module = "Register", feature = "Already Have Account navigation link", priority = "Low")
    public void testAlreadyHaveAccountNavigation() {
        LoginPage login = new LoginPage(driver);
        login.clickSignUp();
        RegisterPage reg = new RegisterPage(driver);
        reg.clickLoginLink();
    }

    @Test
    @TestMetadata(testId = "TC_APP_014", module = "Login", feature = "Privacy Policy link navigation", priority = "Low")
    public void testPrivacyPolicyLink() {
        LoginPage login = new LoginPage(driver);
    }

    @Test
    @TestMetadata(testId = "TC_APP_015", module = "Login", feature = "Terms of Service link navigation", priority = "Low")
    public void testTermsOfServiceLink() {
        LoginPage login = new LoginPage(driver);
    }
}
