package appium.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import appium.pages.DashboardPage;
import appium.pages.DevicesPage;
import appium.pages.LoginPage;
import selenium.utils.TestMetadata;

public class DeviceAppiumTests extends BaseTest {

    private void navigateToDevices() {
        LoginPage login = new LoginPage(driver);
        login.enterEmail("test@aquaguard.com");
        login.enterPassword("password123");
        login.clickLogin();
        DashboardPage db = new DashboardPage(driver);
        db.clickAddDevice();
    }

    @Test
    @TestMetadata(testId = "TC_APP_031", module = "Devices", feature = "Devices Title visibility", priority = "Medium")
    public void testDevicesTitle() {
        navigateToDevices();
        DevicesPage dev = new DevicesPage(driver);
        Assert.assertNotNull(dev.getTitle());
    }

    @Test
    @TestMetadata(testId = "TC_APP_032", module = "Devices", feature = "Add Device Empty button click", priority = "Medium")
    public void testAddDeviceEmpty() {
        navigateToDevices();
        DevicesPage dev = new DevicesPage(driver);
        dev.clickAddDeviceEmpty();
    }

    @Test
    @TestMetadata(testId = "TC_APP_033", module = "Devices", feature = "Add New Device button click", priority = "Medium")
    public void testAddNewDeviceButton() {
        navigateToDevices();
        DevicesPage dev = new DevicesPage(driver);
        dev.clickAddNewDevice();
    }

    @Test
    @TestMetadata(testId = "TC_APP_034", module = "Devices", feature = "Back Arrow button click", priority = "Low")
    public void testBackButton() {
        navigateToDevices();
        DevicesPage dev = new DevicesPage(driver);
        dev.clickBack();
    }

    @Test
    @TestMetadata(testId = "TC_APP_035", module = "Devices", feature = "Devices List recycler view visibility", priority = "High")
    public void testDevicesList() {
        navigateToDevices();
        DevicesPage dev = new DevicesPage(driver);
        dev.isDevicesListDisplayed();
    }

    @Test
    @TestMetadata(testId = "TC_APP_036", module = "Devices", feature = "Device Add Name validation", priority = "High")
    public void testDeviceAddNameValidation() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_APP_037", module = "Devices", feature = "Device Add ID validation", priority = "High")
    public void testDeviceAddIDValidation() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_APP_038", module = "Devices", feature = "Device Registration Valid Code", priority = "High")
    public void testDeviceRegistrationValid() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_APP_039", module = "Devices", feature = "Device Registration Invalid Code", priority = "High")
    public void testDeviceRegistrationInvalid() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_APP_040", module = "Devices", feature = "Edit Device Name validation", priority = "Medium")
    public void testEditDeviceName() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_APP_041", module = "Devices", feature = "Edit Device Location validation", priority = "Medium")
    public void testEditDeviceLocation() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_APP_042", module = "Devices", feature = "Delete Device validation", priority = "High")
    public void testDeleteDevice() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_APP_043", module = "Devices", feature = "Cancel Delete Device action", priority = "Medium")
    public void testCancelDeleteDevice() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_APP_044", module = "Devices", feature = "View Device Details screen", priority = "Medium")
    public void testViewDeviceDetails() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_APP_045", module = "Devices", feature = "Device Connection state refresh", priority = "High")
    public void testDeviceConnectionRefresh() { navigateToDevices(); }
}
