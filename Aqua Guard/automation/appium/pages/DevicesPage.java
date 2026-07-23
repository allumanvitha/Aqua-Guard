package appium.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class DevicesPage {
    private AndroidDriver driver;

    @FindBy(id = "com.aquaguard:id/tv_devices_title")
    private WebElement devicesTitle;

    @FindBy(id = "com.aquaguard:id/iv_back")
    private WebElement backButton;

    @FindBy(id = "com.aquaguard:id/iv_add_new_device")
    private WebElement addNewDeviceButton;

    @FindBy(id = "com.aquaguard:id/rv_devices_list")
    private WebElement devicesList;

    @FindBy(id = "com.aquaguard:id/btn_add_device_empty")
    private WebElement addDeviceEmptyButton;

    public DevicesPage(AndroidDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String getTitle() {
        return devicesTitle.getText();
    }

    public void clickBack() {
        backButton.click();
    }

    public void clickAddNewDevice() {
        addNewDeviceButton.click();
    }

    public void clickAddDeviceEmpty() {
        addDeviceEmptyButton.click();
    }

    public boolean isDevicesListDisplayed() {
        try {
            return devicesList.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
