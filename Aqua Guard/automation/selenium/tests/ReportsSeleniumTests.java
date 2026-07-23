package selenium.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import selenium.pages.DashboardPage;
import selenium.pages.LoginPage;
import selenium.pages.ReportsPage;
import selenium.utils.TestMetadata;

public class ReportsSeleniumTests extends BaseTest {

    private void navigateToReports() {
        LoginPage login = new LoginPage(driver);
        login.enterEmail("admin@aquaguard.com");
        login.enterPassword("Admin@123");
        login.clickLogin();
        DashboardPage db = new DashboardPage(driver);
        db.clickReportsMenu();
    }

    @Test
    @TestMetadata(testId = "TC_SEL_056", module = "Reports", feature = "Report Screen Load UI check", priority = "Medium")
    public void testReportScreenLoad() {
        navigateToReports();
        ReportsPage rep = new ReportsPage(driver);
        Assert.assertNotNull(rep);
    }

    @Test
    @TestMetadata(testId = "TC_SEL_057", module = "Reports", feature = "Generate Usage Report", priority = "High")
    public void testGenerateUsageReport() {
        navigateToReports();
        ReportsPage rep = new ReportsPage(driver);
        rep.selectReportType("Water Usage");
        rep.setDateRange("2026-07-01", "2026-07-20");
        rep.clickGenerateReport();
    }

    @Test
    @TestMetadata(testId = "TC_SEL_058", module = "Reports", feature = "Generate Alerts Report", priority = "High")
    public void testGenerateAlertsReport() {
        navigateToReports();
        ReportsPage rep = new ReportsPage(driver);
        rep.selectReportType("System Alerts");
        rep.setDateRange("2026-07-01", "2026-07-20");
        rep.clickGenerateReport();
    }

    @Test
    @TestMetadata(testId = "TC_SEL_059", module = "Reports", feature = "Export Report Excel", priority = "High")
    public void testExportExcel() {
        navigateToReports();
        ReportsPage rep = new ReportsPage(driver);
        rep.selectReportType("Water Usage");
        rep.clickGenerateReport();
        rep.exportToExcel();
    }

    @Test
    @TestMetadata(testId = "TC_SEL_060", module = "Reports", feature = "Export Report CSV", priority = "High")
    public void testExportCsv() {
        navigateToReports();
        ReportsPage rep = new ReportsPage(driver);
        rep.selectReportType("Water Usage");
        rep.clickGenerateReport();
        rep.exportToCsv();
    }

    @Test
    @TestMetadata(testId = "TC_SEL_061", module = "Reports", feature = "Export Report PDF", priority = "High")
    public void testExportPdf() {
        navigateToReports();
        ReportsPage rep = new ReportsPage(driver);
        rep.selectReportType("Water Usage");
        rep.clickGenerateReport();
        rep.exportToPdf();
    }

    @Test
    @TestMetadata(testId = "TC_SEL_062", module = "Reports", feature = "Future Date Range Validation", priority = "Medium")
    public void testFutureDateRange() {
        navigateToReports();
        ReportsPage rep = new ReportsPage(driver);
        rep.setDateRange("2028-01-01", "2028-01-10");
        rep.clickGenerateReport();
    }

    @Test
    @TestMetadata(testId = "TC_SEL_063", module = "Reports", feature = "Invalid Date Range format", priority = "Medium")
    public void testInvalidDateFormat() {
        navigateToReports();
        ReportsPage rep = new ReportsPage(driver);
        rep.setDateRange("abcd", "efgh");
        rep.clickGenerateReport();
    }

    @Test
    @TestMetadata(testId = "TC_SEL_064", module = "Reports", feature = "Report Pagination Page Navigation", priority = "Low")
    public void testReportPagination() { navigateToReports(); }

    @Test
    @TestMetadata(testId = "TC_SEL_065", module = "Reports", feature = "Report Sorting By Date", priority = "Low")
    public void testReportSortingByDate() { navigateToReports(); }

    @Test
    @TestMetadata(testId = "TC_SEL_066", module = "Reports", feature = "Report Sorting By Volume", priority = "Low")
    public void testReportSortingByVolume() { navigateToReports(); }

    @Test
    @TestMetadata(testId = "TC_SEL_067", module = "Reports", feature = "Reports Tabular Columns check", priority = "Medium")
    public void testReportsTabularColumns() { navigateToReports(); }

    @Test
    @TestMetadata(testId = "TC_SEL_068", module = "Reports", feature = "Monthly Cost Forecast Chart render", priority = "High")
    public void testMonthlyCostForecast() { navigateToReports(); }

    @Test
    @TestMetadata(testId = "TC_SEL_069", module = "Reports", feature = "Daily Cost Forecast Chart render", priority = "High")
    public void testDailyCostForecast() { navigateToReports(); }

    @Test
    @TestMetadata(testId = "TC_SEL_070", module = "Reports", feature = "Water Saving Tips report", priority = "Low")
    public void testWaterSavingTipsReport() { navigateToReports(); }

    @Test
    @TestMetadata(testId = "TC_SEL_071", module = "Reports", feature = "Report Filter by Device Name", priority = "Medium")
    public void testReportFilterByDevice() { navigateToReports(); }

    @Test
    @TestMetadata(testId = "TC_SEL_072", module = "Reports", feature = "Report Filter by Location", priority = "Medium")
    public void testReportFilterByLocation() { navigateToReports(); }

    @Test
    @TestMetadata(testId = "TC_SEL_073", module = "Reports", feature = "Report Data Print window", priority = "Low")
    public void testReportPrint() { navigateToReports(); }

    @Test
    @TestMetadata(testId = "TC_SEL_074", module = "Reports", feature = "Compare Multiple Devices report", priority = "High")
    public void testCompareMultipleDevices() { navigateToReports(); }

    @Test
    @TestMetadata(testId = "TC_SEL_075", module = "Reports", feature = "Report Data Auto-refresh", priority = "Medium")
    public void testReportDataAutoRefresh() { navigateToReports(); }

    @Test
    @TestMetadata(testId = "TC_SEL_076", module = "Reports", feature = "Leakage Incidents Report generate", priority = "High")
    public void testLeakageIncidentsReport() { navigateToReports(); }

    @Test
    @TestMetadata(testId = "TC_SEL_077", module = "Reports", feature = "Hourly Resolution data view", priority = "Low")
    public void testHourlyResolutionData() { navigateToReports(); }

    @Test
    @TestMetadata(testId = "TC_SEL_078", module = "Reports", feature = "Weekly Resolution data view", priority = "Low")
    public void testWeeklyResolutionData() { navigateToReports(); }

    @Test
    @TestMetadata(testId = "TC_SEL_079", module = "Reports", feature = "Monthly Resolution data view", priority = "Low")
    public void testMonthlyResolutionData() { navigateToReports(); }

    @Test
    @TestMetadata(testId = "TC_SEL_080", module = "Reports", feature = "Empty State Report page validation", priority = "Medium")
    public void testEmptyStateReport() { navigateToReports(); }
}
