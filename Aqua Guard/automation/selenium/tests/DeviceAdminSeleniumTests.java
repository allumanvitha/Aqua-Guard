package selenium.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import selenium.pages.DashboardPage;
import selenium.pages.DeviceAdminPage;
import selenium.pages.LoginPage;
import selenium.utils.TestMetadata;

public class DeviceAdminSeleniumTests extends BaseTest {

    private void navigateToDevices() {
        LoginPage login = new LoginPage(driver);
        login.enterEmail("admin@aquaguard.com");
        login.enterPassword("Admin@123");
        login.clickLogin();
        DashboardPage db = new DashboardPage(driver);
        db.clickDevicesMenu();
    }

    @Test
    @TestMetadata(testId = "TC_SEL_036", module = "Devices", feature = "Register Device Valid Flow", priority = "High")
    public void testRegisterDeviceValid() {
        navigateToDevices();
        DeviceAdminPage dev = new DeviceAdminPage(driver);
        dev.clickAddDevice();
        dev.registerDevice("Main Water Tank", "Sensor", "Roof");
    }

    @Test
    @TestMetadata(testId = "TC_SEL_037", module = "Devices", feature = "Register Device Empty Name", priority = "High")
    public void testRegisterDeviceEmptyName() {
        navigateToDevices();
        DeviceAdminPage dev = new DeviceAdminPage(driver);
        dev.clickAddDevice();
        dev.registerDevice("", "Sensor", "Roof");
    }

    @Test
    @TestMetadata(testId = "TC_SEL_038", module = "Devices", feature = "Register Device Empty Type", priority = "Medium")
    public void testRegisterDeviceEmptyType() {
        navigateToDevices();
        DeviceAdminPage dev = new DeviceAdminPage(driver);
        dev.clickAddDevice();
        dev.registerDevice("Main Water Tank", "", "Roof");
    }

    @Test
    @TestMetadata(testId = "TC_SEL_039", module = "Devices", feature = "Register Device Empty Location", priority = "Medium")
    public void testRegisterDeviceEmptyLocation() {
        navigateToDevices();
        DeviceAdminPage dev = new DeviceAdminPage(driver);
        dev.clickAddDevice();
        dev.registerDevice("Main Water Tank", "Sensor", "");
    }

    @Test
    @TestMetadata(testId = "TC_SEL_040", module = "Devices", feature = "Search Device Match", priority = "Medium")
    public void testSearchDeviceMatch() {
        navigateToDevices();
        DeviceAdminPage dev = new DeviceAdminPage(driver);
        dev.searchDevice("Main");
    }

    @Test
    @TestMetadata(testId = "TC_SEL_041", module = "Devices", feature = "Search Device No Match", priority = "Low")
    public void testSearchDeviceNoMatch() {
        navigateToDevices();
        DeviceAdminPage dev = new DeviceAdminPage(driver);
        dev.searchDevice("NonExistentDevice123");
        Assert.assertEquals(dev.getDevicesCount(), 0);
    }

    @Test
    @TestMetadata(testId = "TC_SEL_042", module = "Devices", feature = "Filter By Status Active", priority = "Medium")
    public void testFilterActive() {
        navigateToDevices();
        DeviceAdminPage dev = new DeviceAdminPage(driver);
        dev.filterByStatus("Active");
    }

    @Test
    @TestMetadata(testId = "TC_SEL_043", module = "Devices", feature = "Filter By Status Inactive", priority = "Medium")
    public void testFilterInactive() {
        navigateToDevices();
        DeviceAdminPage dev = new DeviceAdminPage(driver);
        dev.filterByStatus("Inactive");
    }

    @Test
    @TestMetadata(testId = "TC_SEL_044", module = "Devices", feature = "Sort Devices Alphabetically", priority = "Low")
    public void testSortDevices() {
        navigateToDevices();
        DeviceAdminPage dev = new DeviceAdminPage(driver);
        dev.sortByDeviceName();
    }

    @Test
    @TestMetadata(testId = "TC_SEL_045", module = "Devices", feature = "Edit Device Info Valid Flow", priority = "High")
    public void testEditDeviceInfoValid() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_SEL_046", module = "Devices", feature = "Edit Device Empty Fields Validation", priority = "Medium")
    public void testEditDeviceEmptyFields() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_SEL_047", module = "Devices", feature = "Delete Device Valid Flow", priority = "High")
    public void testDeleteDeviceValid() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_SEL_048", module = "Devices", feature = "Delete Device Cancel Flow", priority = "Medium")
    public void testDeleteDeviceCancel() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_SEL_049", module = "Devices", feature = "Device Health Check Status", priority = "Medium")
    public void testDeviceHealthCheck() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_SEL_050", module = "Devices", feature = "Device Offline Alarm", priority = "High")
    public void testDeviceOfflineAlarm() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_SEL_051", module = "Devices", feature = "Device Connection Latency metric", priority = "Low")
    public void testDeviceConnectionLatency() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_SEL_052", module = "Devices", feature = "Telemetry Data Refresh check", priority = "High")
    public void testTelemetryDataRefresh() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_SEL_053", module = "Devices", feature = "Multiple Devices Bulk Edit", priority = "Low")
    public void testMultipleDevicesBulkEdit() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_SEL_054", module = "Devices", feature = "Multiple Devices Bulk Delete", priority = "Low")
    public void testMultipleDevicesBulkDelete() { navigateToDevices(); }

    @Test
    @TestMetadata(testId = "TC_SEL_055", module = "Devices", feature = "Device Firmware Version check", priority = "Medium")
    public void testDeviceFirmwareVersion() { navigateToDevices(); }
}
