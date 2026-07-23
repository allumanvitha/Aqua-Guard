package appium.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class DashboardPage {
    private AndroidDriver driver;

    @FindBy(id = "com.aquaguard:id/tv_dashboard_title")
    private WebElement dashboardTitle;

    @FindBy(id = "com.aquaguard:id/iv_ai_assistant")
    private WebElement aiAssistantButton;

    @FindBy(id = "com.aquaguard:id/iv_add_device")
    private WebElement addDeviceHeaderButton;

    @FindBy(id = "com.aquaguard:id/rv_devices")
    private WebElement devicesRecyclerView;

    @FindBy(id = "com.aquaguard:id/card_no_devices")
    private WebElement noDevicesCard;

    public DashboardPage(AndroidDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String getDashboardTitle() {
        return dashboardTitle.getText();
    }

    public void clickAIAssistant() {
        aiAssistantButton.click();
    }

    public void clickAddDevice() {
        addDeviceHeaderButton.click();
    }

    public boolean isNoDevicesCardDisplayed() {
        try {
            return noDevicesCard.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
