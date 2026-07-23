package selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class DashboardPage {
    private WebDriver driver;

    @FindBy(id = "user-profile-menu")
    private WebElement userProfileMenu;

    @FindBy(id = "logout-btn")
    private WebElement logoutButton;

    @FindBy(id = "water-level-widget")
    private WebElement waterLevelWidget;

    @FindBy(id = "active-devices-count")
    private WebElement activeDevicesCount;

    @FindBy(id = "alerts-badge")
    private WebElement alertsBadge;

    @FindBy(id = "theme-toggle")
    private WebElement themeToggle;

    @FindBy(id = "nav-devices")
    private WebElement navDevicesLink;

    @FindBy(id = "nav-reports")
    private WebElement navReportsLink;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void openProfileMenu() {
        userProfileMenu.click();
    }

    public void clickLogout() {
        logoutButton.click();
    }

    public String getWaterLevel() {
        return waterLevelWidget.getText();
    }

    public String getActiveDevicesCount() {
        return activeDevicesCount.getText();
    }

    public boolean isAlertsBadgeDisplayed() {
        return alertsBadge.isDisplayed();
    }

    public void toggleTheme() {
        themeToggle.click();
    }

    public void clickDevicesMenu() {
        navDevicesLink.click();
    }

    public void clickReportsMenu() {
        navReportsLink.click();
    }
}
