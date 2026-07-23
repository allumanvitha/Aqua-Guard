package selenium.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import com.opencsv.CSVWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ReportHelper {

    public static class TestResult {
        public String testId;
        public String module;
        public String feature;
        public String priority;
        public String environment;
        public String browserOrDevice;
        public String expected;
        public String actual;
        public String status;
        public String screenshot;
        public long executionTime;
        public String remarks;

        public TestResult(String testId, String module, String feature, String priority, String environment,
                          String browserOrDevice, String expected, String actual, String status,
                          String screenshot, long executionTime, String remarks) {
            this.testId = testId;
            this.module = module;
            this.feature = feature;
            this.priority = priority;
            this.environment = environment;
            this.browserOrDevice = browserOrDevice;
            this.expected = expected;
            this.actual = actual;
            this.status = status;
            this.screenshot = screenshot;
            this.executionTime = executionTime;
            this.remarks = remarks;
        }
    }

    private static List<TestResult> seleniumResults = new ArrayList<>();
    private static List<TestResult> appiumResults = new ArrayList<>();

    public static synchronized void addResult(String type, String testId, String module, String feature, String priority,
                                 String env, String browserOrDevice, String expected, String actual,
                                 String status, String screenshot, long time, String remarks) {
        TestResult result = new TestResult(testId, module, feature, priority, env, browserOrDevice, expected, actual, status, screenshot, time, remarks);
        if ("selenium".equalsIgnoreCase(type)) {
            seleniumResults.add(result);
        } else {
            appiumResults.add(result);
        }
    }

    public static void generateReports(String type) {
        List<TestResult> results = "selenium".equalsIgnoreCase(type) ? seleniumResults : appiumResults;
        String baseDir = "automation/" + type.toLowerCase() + "/reports/";
        
        try {
            Files.createDirectories(Paths.get(baseDir));
            generateExcelReport(results, baseDir + "TestReport.xlsx");
            generateHTMLReport(results, baseDir + "TestReport.html", type);
            generateCSVReport(results, baseDir + "TestReport.csv");
            generateJSONReport(results, baseDir + "TestReport.json");
            generateXMLReport(results, baseDir + "TestReport.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateExcelReport(List<TestResult> results, String path) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Test Execution Summary");

        // Styling
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setBorderBottom(BorderStyle.THIN);

        XSSFCellStyle passStyle = workbook.createCellStyle();
        passStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        passStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFCellStyle failStyle = workbook.createCellStyle();
        failStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
        failStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        String[] headers = {"Test ID", "Module", "Feature", "Priority", "Environment", "Browser/Device", 
                            "Expected", "Actual", "Status", "PASS", "FAIL", "Screenshot", "Time (ms)", "Remarks"};
        
        XSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        int passCount = 0;
        int failCount = 0;
        for (TestResult r : results) {
            XSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(r.testId);
            row.createCell(1).setCellValue(r.module);
            row.createCell(2).setCellValue(r.feature);
            row.createCell(3).setCellValue(r.priority);
            row.createCell(4).setCellValue(r.environment);
            row.createCell(5).setCellValue(r.browserOrDevice);
            row.createCell(6).setCellValue(r.expected);
            row.createCell(7).setCellValue(r.actual);
            
            XSSFCell statusCell = row.createCell(8);
            statusCell.setCellValue(r.status);
            if ("PASS".equalsIgnoreCase(r.status)) {
                statusCell.setCellStyle(passStyle);
                row.createCell(9).setCellValue(1);
                row.createCell(10).setCellValue(0);
                passCount++;
            } else {
                statusCell.setCellStyle(failStyle);
                row.createCell(9).setCellValue(0);
                row.createCell(10).setCellValue(1);
                failCount++;
            }

            row.createCell(11).setCellValue(r.screenshot != null ? r.screenshot : "N/A");
            row.createCell(12).setCellValue(r.executionTime);
            row.createCell(13).setCellValue(r.remarks != null ? r.remarks : "");
        }

        // Add summary row
        XSSFRow summaryRow = sheet.createRow(rowNum);
        summaryRow.createCell(0).setCellValue("Total Pass: " + passCount + " | Total Fail: " + failCount);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 13));

        // Create Excel chart
        try {
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 15, 2, 23, 15);
            XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText("Test Pass/Fail Statistics");
            chart.setTitleOverlay(false);

            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.BOTTOM);

            XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromArray(new String[] {"PASS", "FAIL"});
            XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromArray(new Double[] {(double)passCount, (double)failCount});

            XDDFPieChartData data = (XDDFPieChartData) chart.createData(ChartTypes.PIE, null, null);
            XDDFPieChartData.Series series = (XDDFPieChartData.Series) data.addSeries(categories, values);
            series.setTitle("Results", null);
            chart.plot(data);
        } catch (Exception ex) {
            // Ignore chart rendering errors to avoid build failure if environment lacks graphics support
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream fileOutputStream = new FileOutputStream(path);
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        workbook.close();
    }

    private static void generateHTMLReport(List<TestResult> results, String path, String type) throws IOException {
        int passCount = 0;
        int failCount = 0;
        long totalTime = 0;
        StringBuilder rowsHtml = new StringBuilder();

        for (TestResult r : results) {
            boolean isPass = "PASS".equalsIgnoreCase(r.status);
            if (isPass) passCount++;
            else failCount++;
            totalTime += r.executionTime;

            String badgeClass = isPass ? "badge-success" : "badge-danger";
            String screenshotLink = (r.screenshot != null && !r.screenshot.isEmpty() && !"N/A".equals(r.screenshot))
                    ? "<a href='screenshots/" + Paths.get(r.screenshot).getFileName().toString() + "' target='_blank'>View Screenshot</a>"
                    : "N/A";

            rowsHtml.append("<tr>")
                    .append("<td>").append(r.testId).append("</td>")
                    .append("<td>").append(r.module).append("</td>")
                    .append("<td>").append(r.feature).append("</td>")
                    .append("<td>").append(r.priority).append("</td>")
                    .append("<td>").append(r.browserOrDevice).append("</td>")
                    .append("<td><span class='badge ").append(badgeClass).append("'>").append(r.status).append("</span></td>")
                    .append("<td>").append(screenshotLink).append("</td>")
                    .append("<td>").append(r.executionTime).append(" ms</td>")
                    .append("<td>").append(r.remarks != null ? r.remarks : "").append("</td>")
                    .append("</tr>\n");
        }

        int totalTests = passCount + failCount;
        double passPercentage = totalTests > 0 ? ((double) passCount / totalTests) * 100 : 0.0;
        double failPercentage = totalTests > 0 ? ((double) failCount / totalTests) * 100 : 0.0;

        String template = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Aqua Guard QA Execution Dashboard - " + type + "</title>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n" +
                "    <style>\n" +
                "        body { font-family: 'Inter', sans-serif; background-color: #f4f6f9; margin: 0; padding: 20px; }\n" +
                "        .header { display: flex; justify-content: space-between; align-items: center; background: #1a2035; color: white; padding: 15px 30px; border-radius: 8px; margin-bottom: 20px; }\n" +
                "        .dashboard-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 20px; }\n" +
                "        .card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); text-align: center; }\n" +
                "        .card h3 { margin: 0 0 10px 0; color: #888; font-size: 14px; }\n" +
                "        .card p { margin: 0; font-size: 28px; font-weight: bold; color: #333; }\n" +
                "        .chart-container { display: flex; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); margin-bottom: 20px; justify-content: center; align-items: center; height: 300px; }\n" +
                "        .table-container { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow-x: auto; }\n" +
                "        table { width: 100%; border-collapse: collapse; text-align: left; }\n" +
                "        th, td { padding: 12px 15px; border-bottom: 1px solid #eee; }\n" +
                "        th { background-color: #f8f9fa; color: #555; }\n" +
                "        .badge { padding: 5px 10px; border-radius: 4px; font-size: 12px; font-weight: bold; color: white; }\n" +
                "        .badge-success { background-color: #28a745; }\n" +
                "        .badge-danger { background-color: #dc3545; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"header\">\n" +
                "        <h2>Aqua Guard QA Test Execution Dashboard (" + type.toUpperCase() + ")</h2>\n" +
                "        <span id=\"time\">" + java.time.LocalDateTime.now() + "</span>\n" +
                "    </div>\n" +
                "    <div class=\"dashboard-cards\">\n" +
                "        <div class=\"card\"><h3>Total Tests</h3><p>" + totalTests + "</p></div>\n" +
                "        <div class=\"card\" style=\"border-bottom: 4px solid #28a745;\"><h3>PASS</h3><p style=\"color:#28a745;\">" + passCount + " (" + String.format("%.1f", passPercentage) + "%)</p></div>\n" +
                "        <div class=\"card\" style=\"border-bottom: 4px solid #dc3545;\"><h3>FAIL</h3><p style=\"color:#dc3545;\">" + failCount + " (" + String.format("%.1f", failPercentage) + "%)</p></div>\n" +
                "        <div class=\"card\"><h3>Total Duration</h3><p>" + String.format("%.2f", (double)totalTime / 1000) + " s</p></div>\n" +
                "    </div>\n" +
                "    <div class=\"chart-container\">\n" +
                "        <canvas id=\"pieChart\" style=\"max-width: 250px; max-height: 250px;\"></canvas>\n" +
                "    </div>\n" +
                "    <div class=\"table-container\">\n" +
                "        <table>\n" +
                "            <thead>\n" +
                "                <tr>\n" +
                "                    <th>Test ID</th>\n" +
                "                    <th>Module</th>\n" +
                "                    <th>Feature</th>\n" +
                "                    <th>Priority</th>\n" +
                "                    <th>Browser/Device</th>\n" +
                "                    <th>Status</th>\n" +
                "                    <th>Screenshot</th>\n" +
                "                    <th>Time</th>\n" +
                "                    <th>Remarks</th>\n" +
                "                </tr>\n" +
                "            </thead>\n" +
                "            <tbody>\n" +
                "                " + rowsHtml.toString() + "\n" +
                "            </tbody>\n" +
                "        </table>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        const ctx = document.getElementById('pieChart').getContext('2d');\n" +
                "        new Chart(ctx, {\n" +
                "            type: 'pie',\n" +
                "            data: {\n" +
                "                labels: ['PASS', 'FAIL'],\n" +
                "                datasets: [{\n" +
                "                    data: [" + passCount + ", " + failCount + "],\n" +
                "                    backgroundColor: ['#28a745', '#dc3545'],\n" +
                "                    borderWidth: 1\n" +
                "                }]\n" +
                "            },\n" +
                "            options: {\n" +
                "                responsive: true,\n" +
                "                plugins: {\n" +
                "                    legend: {\n" +
                "                        position: 'bottom',\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";

        Files.write(Paths.get(path), template.getBytes());
    }

    private static void generateCSVReport(List<TestResult> results, String path) throws IOException {
        Writer writer = new FileWriter(path);
        CSVWriter csvWriter = new CSVWriter(writer);
        String[] headers = {"TestID", "Module", "Feature", "Priority", "Environment", "BrowserOrDevice", "Expected", "Actual", "Status", "Screenshot", "ExecutionTimeMs", "Remarks"};
        csvWriter.writeNext(headers);

        for (TestResult r : results) {
            String[] data = {r.testId, r.module, r.feature, r.priority, r.environment, r.browserOrDevice, r.expected, r.actual, r.status, r.screenshot != null ? r.screenshot : "N/A", String.valueOf(r.executionTime), r.remarks != null ? r.remarks : ""};
            csvWriter.writeNext(data);
        }
        csvWriter.close();
        writer.close();
    }

    private static void generateJSONReport(List<TestResult> results, String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File(path), results);
    }

    private static void generateXMLReport(List<TestResult> results, String path) throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File(path), results);
    }
}
