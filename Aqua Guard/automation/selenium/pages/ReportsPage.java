package selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ReportsPage {
    private WebDriver driver;

    @FindBy(id = "report-type-select")
    private WebElement reportTypeSelect;

    @FindBy(id = "start-date")
    private WebElement startDateInput;

    @FindBy(id = "end-date")
    private WebElement endDateInput;

    @FindBy(id = "generate-report-btn")
    private WebElement generateReportBtn;

    @FindBy(id = "export-excel-btn")
    private WebElement exportExcelBtn;

    @FindBy(id = "export-csv-btn")
    private WebElement exportCsvBtn;

    @FindBy(id = "export-pdf-btn")
    private WebElement exportPdfBtn;

    @FindBy(id = "report-table")
    private WebElement reportTable;

    public ReportsPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void selectReportType(String type) {
        reportTypeSelect.sendKeys(type);
    }

    public void setDateRange(String start, String end) {
        startDateInput.sendKeys(start);
        endDateInput.sendKeys(end);
    }

    public void clickGenerateReport() {
        generateReportBtn.click();
    }

    public void exportToExcel() {
        exportExcelBtn.click();
    }

    public void exportToCsv() {
        exportCsvBtn.click();
    }

    public void exportToPdf() {
        exportPdfBtn.click();
    }

    public boolean isReportTableDisplayed() {
        return reportTable.isDisplayed();
    }
}
