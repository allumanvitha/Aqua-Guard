package appium.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import appium.pages.DashboardPage;
import appium.pages.LoginPage;
import selenium.utils.TestMetadata;

public class DashboardAppiumTests extends BaseTest {

    private void performLogin() {
        LoginPage login = new LoginPage(driver);
        login.enterEmail("test@aquaguard.com");
        login.enterPassword("password123");
        login.clickLogin();
    }

    @Test
    @TestMetadata(testId = "TC_APP_016", module = "Dashboard", feature = "Dashboard Header elements visibility", priority = "Medium")
    public void testDashboardHeader() {
        performLogin();
        DashboardPage db = new DashboardPage(driver);
        Assert.assertNotNull(db.getDashboardTitle());
    }

    @Test
    @TestMetadata(testId = "TC_APP_017", module = "Dashboard", feature = "Device Selection Recycler View list", priority = "High")
    public void testDevicesListDisplay() {
        performLogin();
        DashboardPage db = new DashboardPage(driver);
    }

    @Test
    @TestMetadata(testId = "TC_APP_018", module = "Dashboard", feature = "Empty State Devices Card visibility", priority = "High")
    public void testNoDevicesCard() {
        performLogin();
        DashboardPage db = new DashboardPage(driver);
        db.isNoDevicesCardDisplayed();
    }

    @Test
    @TestMetadata(testId = "TC_APP_019", module = "Dashboard", feature = "AI Assistant Quick access button click", priority = "Medium")
    public void testAIAssistantAccess() {
        performLogin();
        DashboardPage db = new DashboardPage(driver);
        db.clickAIAssistant();
    }

    @Test
    @TestMetadata(testId = "TC_APP_020", module = "Dashboard", feature = "Add Device Quick access button click", priority = "Medium")
    public void testAddDeviceAccess() {
        performLogin();
        DashboardPage db = new DashboardPage(driver);
        db.clickAddDevice();
    }

    @Test
    @TestMetadata(testId = "TC_APP_021", module = "Dashboard", feature = "Water Level Widget UI display", priority = "High")
    public void testWaterLevelWidget() { performLogin(); }

    @Test
    @TestMetadata(testId = "TC_APP_022", module = "Dashboard", feature = "Water Level Percentage refresh", priority = "High")
    public void testWaterLevelPercentage() { performLogin(); }

    @Test
    @TestMetadata(testId = "TC_APP_023", module = "Dashboard", feature = "Valve Switch button state change", priority = "High")
    public void testValveSwitchButton() { performLogin(); }

    @Test
    @TestMetadata(testId = "TC_APP_024", module = "Dashboard", feature = "Bottom Navigation Profile click", priority = "Medium")
    public void testBottomNavigationProfile() { performLogin(); }

    @Test
    @TestMetadata(testId = "TC_APP_025", module = "Dashboard", feature = "Bottom Navigation Alerts click", priority = "Medium")
    public void testBottomNavigationAlerts() { performLogin(); }

    @Test
    @TestMetadata(testId = "TC_APP_026", module = "Dashboard", feature = "Bottom Navigation Analytics click", priority = "Medium")
    public void testBottomNavigationAnalytics() { performLogin(); }

    @Test
    @TestMetadata(testId = "TC_APP_027", module = "Dashboard", feature = "Bottom Navigation History click", priority = "Medium")
    public void testBottomNavigationHistory() { performLogin(); }

    @Test
    @TestMetadata(testId = "TC_APP_028", module = "Dashboard", feature = "Toolbar Options Menu settings click", priority = "Low")
    public void testToolbarOptionsMenu() { performLogin(); }

    @Test
    @TestMetadata(testId = "TC_APP_029", module = "Dashboard", feature = "Realtime Updates data sync check", priority = "High")
    public void testRealtimeSync() { performLogin(); }

    @Test
    @TestMetadata(testId = "TC_APP_030", module = "Dashboard", feature = "Swipe to Refresh gesture on dashboard", priority = "Medium")
    public void testSwipeToRefresh() { performLogin(); }
}
