package appium.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ProfileSettingsPage {
    private AndroidDriver driver;

    @FindBy(id = "com.aquaguard:id/tv_profile_title")
    private WebElement profileTitle;

    @FindBy(id = "com.aquaguard:id/tv_profile_name")
    private WebElement profileName;

    @FindBy(id = "com.aquaguard:id/tv_profile_email")
    private WebElement profileEmail;

    @FindBy(id = "com.aquaguard:id/et_family_members")
    private WebElement familyMembersInput;

    @FindBy(id = "com.aquaguard:id/et_daily_target")
    private WebElement dailyTargetInput;

    @FindBy(id = "com.aquaguard:id/btn_save_profile")
    private WebElement saveProfileButton;

    @FindBy(id = "com.aquaguard:id/btn_settings")
    private WebElement settingsButton;

    @FindBy(id = "com.aquaguard:id/btn_sign_out")
    private WebElement signOutButton;

    public ProfileSettingsPage(AndroidDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String getProfileName() {
        return profileName.getText();
    }

    public String getProfileEmail() {
        return profileEmail.getText();
    }

    public void updateHousehold(String familyMembers, String dailyTarget) {
        familyMembersInput.clear();
        familyMembersInput.sendKeys(familyMembers);
        dailyTargetInput.clear();
        dailyTargetInput.sendKeys(dailyTarget);
        saveProfileButton.click();
    }

    public void clickSettings() {
        settingsButton.click();
    }

    public void clickSignOut() {
        signOutButton.click();
    }
}
