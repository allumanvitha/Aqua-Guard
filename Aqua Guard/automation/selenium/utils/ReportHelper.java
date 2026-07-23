package selenium.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import com.opencsv.CSVWriter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import java.awt.Color;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ReportHelper {

    public static class TestResult {
        public String type; // "Appium" or "Selenium"
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

        public TestResult() {} // Default constructor for Jackson deserialization

        public TestResult(String type, String testId, String module, String feature, String priority, String environment,
                          String browserOrDevice, String expected, String actual, String status,
                          String screenshot, long executionTime, String remarks) {
            this.type = type;
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

    private static List<TestResult> currentRunResults = new ArrayList<>();

    public static synchronized void addResult(String type, String testId, String module, String feature, String priority,
                                 String env, String browserOrDevice, String expected, String actual,
                                 String status, String screenshot, long time, String remarks) {
        TestResult result = new TestResult(type, testId, module, feature, priority, env, browserOrDevice, expected, actual, status, screenshot, time, remarks);
        currentRunResults.add(result);
    }

    public static void saveAndGenerateUnifiedReport(String suiteType) {
        String targetDir = "automation/target/";
        String jsonPath = targetDir + suiteType.toLowerCase() + "_results.json";
        ObjectMapper mapper = new ObjectMapper();

        try {
            Files.createDirectories(Paths.get(targetDir));
            // Save current run's results to its JSON cache file
            mapper.writeValue(new File(jsonPath), currentRunResults);

            // Read both JSON files to generate a unified list
            List<TestResult> unifiedResults = new ArrayList<>();
            
            String seleniumPath = targetDir + "selenium_results.json";
            String appiumPath = targetDir + "appium_results.json";

            if (Files.exists(Paths.get(seleniumPath))) {
                List<TestResult> selList = mapper.readValue(new File(seleniumPath), new TypeReference<List<TestResult>>() {});
                unifiedResults.addAll(selList);
            }
            if (Files.exists(Paths.get(appiumPath))) {
                List<TestResult> appList = mapper.readValue(new File(appiumPath), new TypeReference<List<TestResult>>() {});
                unifiedResults.addAll(appList);
            }

            // Fallback to current memory run if file reads are empty
            if (unifiedResults.isEmpty()) {
                unifiedResults.addAll(currentRunResults);
            }

            // Create Unified Reports Directory
            String reportDir = "automation/reports/";
            Files.createDirectories(Paths.get(reportDir));

            // Generate reports
            generateExcelReport(unifiedResults, reportDir + "TestReport.xlsx");
            generateHTMLReport(unifiedResults, reportDir + "TestReport.html");
            generateHTMLReport(unifiedResults, reportDir + "index.html");
            generateCSVReport(unifiedResults, reportDir + "TestReport.csv");
            generateJSONReport(unifiedResults, reportDir + "TestReport.json");
            generateXMLReport(unifiedResults, reportDir + "TestReport.xml");
            generatePDFReport(unifiedResults, reportDir + "TestReport.pdf");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateExcelReport(List<TestResult> results, String path) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Unified Test Report");

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

        String[] headers = {"Test ID", "Type", "Module", "Feature", "Priority", "Environment", "Browser/Device", 
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
            row.createCell(1).setCellValue(r.type);
            row.createCell(2).setCellValue(r.module);
            row.createCell(3).setCellValue(r.feature);
            row.createCell(4).setCellValue(r.priority);
            row.createCell(5).setCellValue(r.environment);
            row.createCell(6).setCellValue(r.browserOrDevice);
            row.createCell(7).setCellValue(r.expected);
            row.createCell(8).setCellValue(r.actual);
            
            XSSFCell statusCell = row.createCell(9);
            statusCell.setCellValue(r.status);
            if ("PASS".equalsIgnoreCase(r.status)) {
                statusCell.setCellStyle(passStyle);
                row.createCell(10).setCellValue(1);
                row.createCell(11).setCellValue(0);
                passCount++;
            } else {
                statusCell.setCellStyle(failStyle);
                row.createCell(10).setCellValue(0);
                row.createCell(11).setCellValue(1);
                failCount++;
            }

            row.createCell(12).setCellValue(r.screenshot != null ? r.screenshot : "N/A");
            row.createCell(13).setCellValue(r.executionTime);
            row.createCell(14).setCellValue(r.remarks != null ? r.remarks : "");
        }

        // Add summary row
        XSSFRow summaryRow = sheet.createRow(rowNum);
        summaryRow.createCell(0).setCellValue("Total Pass: " + passCount + " | Total Fail: " + failCount);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 14));

        // Create Excel chart
        try {
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 16, 2, 24, 15);
            XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText("Unified Test Statistics");
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
            // Ignore graphics compilation problems silently
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream fileOutputStream = new FileOutputStream(path);
        workbook.write(fileOutputStream);
        fileOutputStream.close();
        workbook.close();
    }

    private static void generateHTMLReport(List<TestResult> results, String path) throws IOException {
        int passCount = 0;
        int failCount = 0;
        long totalTime = 0;
        int seleniumCount = 0;
        int appiumCount = 0;
        StringBuilder rowsHtml = new StringBuilder();

        for (TestResult r : results) {
            boolean isPass = "PASS".equalsIgnoreCase(r.status);
            if (isPass) passCount++;
            else failCount++;
            totalTime += r.executionTime;

            if ("Selenium".equalsIgnoreCase(r.type)) seleniumCount++;
            else appiumCount++;

            String badgeClass = isPass ? "badge-success" : "badge-danger";
            String screenshotLink = (r.screenshot != null && !r.screenshot.isEmpty() && !"N/A".equals(r.screenshot))
                    ? "<a href='" + r.screenshot + "' target='_blank'>View Screenshot</a>"
                    : "N/A";

            rowsHtml.append("<tr>")
                    .append("<td>").append(r.testId).append("</td>")
                    .append("<td>").append(r.type).append("</td>")
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
                "    <title>Aqua Guard Consolidated QA Dashboard</title>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n" +
                "    <style>\n" +
                "        body { font-family: 'Inter', sans-serif; background-color: #f4f6f9; margin: 0; padding: 20px; }\n" +
                "        .header { display: flex; justify-content: space-between; align-items: center; background: #1a2035; color: white; padding: 15px 30px; border-radius: 8px; margin-bottom: 20px; }\n" +
                "        .btn-download {\n" +
                "            background-color: #2196f3; color: white; padding: 10px 20px; border-radius: 6px; text-decoration: none; font-weight: bold; \n" +
                "            display: inline-flex; align-items: center; gap: 8px; border: none; cursor: pointer; transition: background-color 0.2s;\n" +
                "        }\n" +
                "        .btn-download:hover { background-color: #0b7dda; }\n" +
                "        .dashboard-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 20px; }\n" +
                "        .card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); text-align: center; }\n" +
                "        .card h3 { margin: 0 0 10px 0; color: #888; font-size: 14px; }\n" +
                "        .card p { margin: 0; font-size: 28px; font-weight: bold; color: #333; }\n" +
                "        .chart-container { display: flex; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); margin-bottom: 20px; justify-content: center; align-items: center; gap: 40px; }\n" +
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
                "        <h2>Aqua Guard Unified QA Automation Hub (Selenium + Appium)</h2>\n" +
                "        <div style=\"display: flex; align-items: center; gap: 20px;\">\n" +
                "            <a href=\"TestReport.xlsx\" download class=\"btn-download\">\n" +
                "                <svg width=\"16\" height=\"16\" fill=\"currentColor\" viewBox=\"0 0 16 16\"><path d=\"M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z\"/><path d=\"M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708l3 3z\"/></svg>\n" +
                "                Download Excel Report\n" +
                "            </a>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <div class=\"dashboard-cards\">\n" +
                "        <div class=\"card\"><h3>Total Tests Ran</h3><p>" + totalTests + "</p></div>\n" +
                "        <div class=\"card\" style=\"border-bottom: 4px solid #28a745;\"><h3>PASS</h3><p style=\"color:#28a745;\">" + passCount + " (" + String.format("%.1f", passPercentage) + "%)</p></div>\n" +
                "        <div class=\"card\" style=\"border-bottom: 4px solid #dc3545;\"><h3>FAIL</h3><p style=\"color:#dc3545;\">" + failCount + " (" + String.format("%.1f", failPercentage) + "%)</p></div>\n" +
                "        <div class=\"card\"><h3>Execution Time</h3><p>" + String.format("%.2f", (double)totalTime / 1000) + " s</p></div>\n" +
                "    </div>\n" +
                "    <div class=\"chart-container\">\n" +
                "        <div>\n" +
                "            <h4 style=\"text-align: center; color: #555;\">Overall Pass/Fail</h4>\n" +
                "            <canvas id=\"pieChart\" style=\"max-width: 200px; max-height: 200px;\"></canvas>\n" +
                "        </div>\n" +
                "        <div>\n" +
                "            <h4 style=\"text-align: center; color: #555;\">Suite Distribution</h4>\n" +
                "            <canvas id=\"distChart\" style=\"max-width: 200px; max-height: 200px;\"></canvas>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <div class=\"table-container\">\n" +
                "        <table>\n" +
                "            <thead>\n" +
                "                <tr>\n" +
                "                    <th>Test ID</th>\n" +
                "                    <th>Type</th>\n" +
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
                "        const ctxPie = document.getElementById('pieChart').getContext('2d');\n" +
                "        new Chart(ctxPie, {\n" +
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
                "                plugins: { legend: { position: 'bottom' } }\n" +
                "            }\n" +
                "        });\n" +
                "\n" +
                "        const ctxDist = document.getElementById('distChart').getContext('2d');\n" +
                "        new Chart(ctxDist, {\n" +
                "            type: 'doughnut',\n" +
                "            data: {\n" +
                "                labels: ['Selenium (Web)', 'Appium (Mobile)'],\n" +
                "                datasets: [{\n" +
                "                    data: [" + seleniumCount + ", " + appiumCount + "],\n" +
                "                    backgroundColor: ['#36a2eb', '#ffce56'],\n" +
                "                    borderWidth: 1\n" +
                "                }]\n" +
                "            },\n" +
                "            options: {\n" +
                "                responsive: true,\n" +
                "                plugins: { legend: { position: 'bottom' } }\n" +
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
        String[] headers = {"TestID", "Type", "Module", "Feature", "Priority", "Environment", "BrowserOrDevice", "Expected", "Actual", "Status", "Screenshot", "ExecutionTimeMs", "Remarks"};
        csvWriter.writeNext(headers);

        for (TestResult r : results) {
            String[] data = {r.testId, r.type, r.module, r.feature, r.priority, r.environment, r.browserOrDevice, r.expected, r.actual, r.status, r.screenshot != null ? r.screenshot : "N/A", String.valueOf(r.executionTime), r.remarks != null ? r.remarks : ""};
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

    private static void generatePDFReport(List<TestResult> results, String path) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            
            com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            Paragraph title = new Paragraph("Aqua Guard QA Test Execution Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);
            
            int passCount = 0;
            int failCount = 0;
            for (TestResult r : results) {
                if ("PASS".equalsIgnoreCase(r.status)) passCount++;
                else failCount++;
            }
            
            com.lowagie.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            document.add(new Paragraph("Execution Summary:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            document.add(new Paragraph("Total Tests: " + results.size(), normalFont));
            document.add(new Paragraph("PASS: " + passCount, normalFont));
            document.add(new Paragraph("FAIL: " + failCount, normalFont));
            document.add(new Paragraph("Pass Rate: " + String.format("%.1f%%", ((double)passCount/results.size())*100), normalFont));
            document.add(new Paragraph("\n"));
            
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.5f, 1f, 1.5f, 3.5f, 1f, 1.5f});
            
            com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            PdfPCell cell = new PdfPCell(new Paragraph("Test ID", headerFont));
            cell.setBackgroundColor(Color.DARK_GRAY);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Type", headerFont));
            cell.setBackgroundColor(Color.DARK_GRAY);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Module", headerFont));
            cell.setBackgroundColor(Color.DARK_GRAY);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Feature", headerFont));
            cell.setBackgroundColor(Color.DARK_GRAY);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Status", headerFont));
            cell.setBackgroundColor(Color.DARK_GRAY);
            table.addCell(cell);
            
            cell = new PdfPCell(new Paragraph("Time (ms)", headerFont));
            cell.setBackgroundColor(Color.DARK_GRAY);
            table.addCell(cell);
            
            com.lowagie.text.Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
            for (TestResult r : results) {
                table.addCell(new Paragraph(r.testId, cellFont));
                table.addCell(new Paragraph(r.type, cellFont));
                table.addCell(new Paragraph(r.module, cellFont));
                table.addCell(new Paragraph(r.feature, cellFont));
                
                PdfPCell statusCell = new PdfPCell(new Paragraph(r.status, cellFont));
                if ("PASS".equalsIgnoreCase(r.status)) {
                    statusCell.setBackgroundColor(Color.GREEN);
                } else {
                    statusCell.setBackgroundColor(Color.RED);
                }
                table.addCell(statusCell);
                
                table.addCell(new Paragraph(String.valueOf(r.executionTime), cellFont));
            }
            
            document.add(table);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}

