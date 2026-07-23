package selenium.tests;

import org.testng.annotations.Test;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import selenium.utils.ConfigReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PerformanceLoadTest {

    @Test
    public void runLoadTest() throws InterruptedException, IOException {
        String targetUrl = ConfigReader.getProperty("selenium.url");
        if (targetUrl == null || targetUrl.isEmpty()) {
            targetUrl = "https://aquaguard-admin.web.app";
        }

        int virtualUsers = 100;
        int durationSeconds = 60;
        long durationMs = durationSeconds * 1000L;

        System.out.println("Starting Baseline/Load Test...");
        System.out.println("Concurrency: " + virtualUsers + " Virtual Users");
        System.out.println("Duration: " + durationSeconds + " seconds");
        System.out.println("Target URL: " + targetUrl);

        ExecutorService executor = Executors.newFixedThreadPool(virtualUsers);
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger totalRequests = new AtomicInteger(0);
        AtomicInteger successRequests = new AtomicInteger(0);
        AtomicInteger failureRequests = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();
        long endTime = startTime + durationMs;

        for (int i = 0; i < virtualUsers; i++) {
            final String urlStr = targetUrl;
            executor.submit(() -> {
                while (System.currentTimeMillis() < endTime) {
                    long reqStart = System.currentTimeMillis();
                    try {
                        URL url = new URL(urlStr);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(5000);
                        conn.setReadTimeout(5000);
                        
                        int code = conn.getResponseCode();
                        long reqEnd = System.currentTimeMillis();
                        long respTime = reqEnd - reqStart;
                        
                        totalRequests.incrementAndGet();
                        if (code >= 200 && code < 400) {
                            successRequests.incrementAndGet();
                            responseTimes.add(respTime);
                        } else {
                            failureRequests.incrementAndGet();
                        }
                        
                        // Small sleep to prevent immediate rate-limiting/DDoS blocking
                        Thread.sleep(50);
                    } catch (Exception e) {
                        totalRequests.incrementAndGet();
                        failureRequests.incrementAndGet();
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(durationSeconds + 10, TimeUnit.SECONDS);

        long actualDurationMs = System.currentTimeMillis() - startTime;
        double durationSec = actualDurationMs / 1000.0;
        double rps = totalRequests.get() / durationSec;

        // Statistics calculation
        long minTime = Long.MAX_VALUE;
        long maxTime = 0;
        long sumTime = 0;
        
        synchronized (responseTimes) {
            if (!responseTimes.isEmpty()) {
                for (long t : responseTimes) {
                    if (t < minTime) minTime = t;
                    if (t > maxTime) maxTime = t;
                    sumTime += t;
                }
            } else {
                minTime = 0;
            }
        }
        
        long avgTime = responseTimes.isEmpty() ? 0 : sumTime / responseTimes.size();

        System.out.println("Load Test Completed.");
        System.out.println("Total Requests: " + totalRequests.get());
        System.out.println("Success: " + successRequests.get());
        System.out.println("Failures: " + failureRequests.get());
        System.out.println("RPS: " + String.format("%.2f", rps));
        System.out.println("Avg Response Time: " + avgTime + " ms");
        System.out.println("Min Response Time: " + minTime + " ms");
        System.out.println("Max Response Time: " + maxTime + " ms");

        // Write results to Excel
        writeLoadTestToExcel(virtualUsers, durationSeconds, totalRequests.get(), successRequests.get(),
                failureRequests.get(), rps, avgTime, minTime, maxTime, responseTimes);
    }

    private void writeLoadTestToExcel(int vus, int duration, int total, int success, int fail, double rps,
                                      long avg, long min, long max, List<Long> times) throws IOException {
        String reportPath = "automation/reports/LoadTestReport.xlsx";
        Files.createDirectories(Paths.get("automation/reports/"));

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Load Test Summary");

        // Cell Style for Header
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // Header Row
        String[] headers = {"Metric", "Value"};
        XSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Summary Data
        String[][] summaryData = {
            {"Virtual Users (Concurrency)", String.valueOf(vus)},
            {"Configured Duration (seconds)", String.valueOf(duration)},
            {"Total Requests Sent", String.valueOf(total)},
            {"Successful Requests", String.valueOf(success)},
            {"Failed Requests", String.valueOf(fail)},
            {"Requests Per Second (RPS)", String.format("%.2f", rps)},
            {"Average Response Time (ms)", String.valueOf(avg)},
            {"Minimum Response Time (ms)", String.valueOf(min)},
            {"Maximum Response Time (ms)", String.valueOf(max)}
        };

        int rowNum = 1;
        for (String[] data : summaryData) {
            XSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(data[0]);
            row.createCell(1).setCellValue(data[1]);
        }

        // Add Raw Response Times Sheet
        XSSFSheet rawSheet = workbook.createSheet("Raw Response Times");
        XSSFRow rawHeader = rawSheet.createRow(0);
        XSSFCell c1 = rawHeader.createCell(0);
        c1.setCellValue("Request Index");
        c1.setCellStyle(headerStyle);
        XSSFCell c2 = rawHeader.createCell(1);
        c2.setCellValue("Response Time (ms)");
        c2.setCellStyle(headerStyle);

        int rawRowNum = 1;
        synchronized (times) {
            int limit = Math.min(times.size(), 5000);
            for (int i = 0; i < limit; i++) {
                XSSFRow row = rawSheet.createRow(rawRowNum++);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(times.get(i));
            }
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        rawSheet.autoSizeColumn(0);
        rawSheet.autoSizeColumn(1);

        try (FileOutputStream fos = new FileOutputStream(reportPath)) {
            workbook.write(fos);
        }
        workbook.close();
        System.out.println("Load test report written to: " + reportPath);
    }
}
