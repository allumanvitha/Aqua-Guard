package selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.util.List;

public class DeviceAdminPage {
    private WebDriver driver;

    @FindBy(id = "add-device-btn")
    private WebElement addDeviceBtn;

    @FindBy(id = "device-name-input")
    private WebElement deviceNameInput;

    @FindBy(id = "device-type-select")
    private WebElement deviceTypeSelect;

    @FindBy(id = "device-location-input")
    private WebElement deviceLocationInput;

    @FindBy(id = "submit-device-btn")
    private WebElement submitDeviceBtn;

    @FindBy(className = "device-row")
    private List<WebElement> deviceRows;

    @FindBy(id = "search-device-input")
    private WebElement searchDeviceInput;

    @FindBy(id = "filter-status-select")
    private WebElement filterStatusSelect;

    @FindBy(id = "sort-name-header")
    private WebElement sortNameHeader;

    public DeviceAdminPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickAddDevice() {
        addDeviceBtn.click();
    }

    public void registerDevice(String name, String type, String location) {
        deviceNameInput.sendKeys(name);
        deviceTypeSelect.sendKeys(type);
        deviceLocationInput.sendKeys(location);
        submitDeviceBtn.click();
    }

    public int getDevicesCount() {
        return deviceRows.size();
    }

    public void searchDevice(String text) {
        searchDeviceInput.clear();
        searchDeviceInput.sendKeys(text);
    }

    public void filterByStatus(String status) {
        filterStatusSelect.sendKeys(status);
    }

    public void sortByDeviceName() {
        sortNameHeader.click();
    }
}
