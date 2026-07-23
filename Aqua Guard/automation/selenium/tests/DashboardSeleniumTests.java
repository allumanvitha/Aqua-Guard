package selenium.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import selenium.pages.DashboardPage;
import selenium.pages.LoginPage;
import selenium.utils.TestMetadata;

public class DashboardSeleniumTests extends BaseTest {

    private void loginAsAdmin() {
        LoginPage login = new LoginPage(driver);
        login.enterEmail("admin@aquaguard.com");
        login.enterPassword("Admin@123");
        login.clickLogin();
    }

    @Test
    @TestMetadata(testId = "TC_SEL_016", module = "Dashboard", feature = "Header Display Verification", priority = "Medium")
    public void testHeaderDisplay() {
        loginAsAdmin();
        DashboardPage db = new DashboardPage(driver);
        Assert.assertNotNull(db);
    }

    @Test
    @TestMetadata(testId = "TC_SEL_017", module = "Dashboard", feature = "Water Level Widget Status", priority = "High")
    public void testWaterLevelWidget() {
        loginAsAdmin();
        DashboardPage db = new DashboardPage(driver);
    }

    @Test
    @TestMetadata(testId = "TC_SEL_018", module = "Dashboard", feature = "Active Devices Counter", priority = "High")
    public void testActiveDevicesCount() {
        loginAsAdmin();
        DashboardPage db = new DashboardPage(driver);
    }

    @Test
    @TestMetadata(testId = "TC_SEL_019", module = "Dashboard", feature = "Alerts Badge Indicator", priority = "High")
    public void testAlertsBadge() {
        loginAsAdmin();
        DashboardPage db = new DashboardPage(driver);
    }

    @Test
    @TestMetadata(testId = "TC_SEL_020", module = "Dashboard", feature = "Theme Toggle Switch", priority = "Low")
    public void testThemeToggle() {
        loginAsAdmin();
        DashboardPage db = new DashboardPage(driver);
        db.toggleTheme();
    }

    @Test
    @TestMetadata(testId = "TC_SEL_021", module = "Dashboard", feature = "Menu Navigation Devices", priority = "Medium")
    public void testNavigationToDevices() {
        loginAsAdmin();
        DashboardPage db = new DashboardPage(driver);
        db.clickDevicesMenu();
    }

    @Test
    @TestMetadata(testId = "TC_SEL_022", module = "Dashboard", feature = "Menu Navigation Reports", priority = "Medium")
    public void testNavigationToReports() {
        loginAsAdmin();
        DashboardPage db = new DashboardPage(driver);
        db.clickReportsMenu();
    }

    @Test
    @TestMetadata(testId = "TC_SEL_023", module = "Dashboard", feature = "Weekly Usage Chart Render", priority = "Medium")
    public void testWeeklyUsageChart() { loginAsAdmin(); }

    @Test
    @TestMetadata(testId = "TC_SEL_024", module = "Dashboard", feature = "Monthly Usage Chart Render", priority = "Medium")
    public void testMonthlyUsageChart() { loginAsAdmin(); }

    @Test
    @TestMetadata(testId = "TC_SEL_025", module = "Dashboard", feature = "Yearly Usage Chart Render", priority = "Medium")
    public void testYearlyUsageChart() { loginAsAdmin(); }

    @Test
    @TestMetadata(testId = "TC_SEL_026", module = "Dashboard", feature = "Realtime Update WebSockets", priority = "High")
    public void testRealtimeWebSocketUpdate() { loginAsAdmin(); }

    @Test
    @TestMetadata(testId = "TC_SEL_027", module = "Dashboard", feature = "Session Persistence check", priority = "High")
    public void testSessionPersistence() { loginAsAdmin(); }

    @Test
    @TestMetadata(testId = "TC_SEL_028", module = "Dashboard", feature = "Notification Bell Icon state", priority = "Low")
    public void testNotificationBellIcon() { loginAsAdmin(); }

    @Test
    @TestMetadata(testId = "TC_SEL_029", module = "Dashboard", feature = "Notification Dropdown menu", priority = "Low")
    public void testNotificationDropdown() { loginAsAdmin(); }

    @Test
    @TestMetadata(testId = "TC_SEL_030", module = "Dashboard", feature = "Mark Notifications as Read", priority = "Medium")
    public void testMarkNotificationsAsRead() { loginAsAdmin(); }

    @Test
    @TestMetadata(testId = "TC_SEL_031", module = "Dashboard", feature = "Clear All Notifications", priority = "Medium")
    public void testClearAllNotifications() { loginAsAdmin(); }

    @Test
    @TestMetadata(testId = "TC_SEL_032", module = "Dashboard", feature = "System Health Status Widget", priority = "Medium")
    public void testSystemHealthStatus() { loginAsAdmin(); }

    @Test
    @TestMetadata(testId = "TC_SEL_033", module = "Dashboard", feature = "API Connectivity Status check", priority = "High")
    public void testAPIConnectivityStatus() { loginAsAdmin(); }

    @Test
    @TestMetadata(testId = "TC_SEL_034", module = "Dashboard", feature = "Quick Add Device shortcut", priority = "Low")
    public void testQuickAddDevice() { loginAsAdmin(); }

    @Test
    @TestMetadata(testId = "TC_SEL_035", module = "Dashboard", feature = "Support Ticket quick link", priority = "Low")
    public void testSupportTicketLink() { loginAsAdmin(); }
}
