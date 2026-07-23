# Aqua Guard - QA Automation Framework

This directory contains a complete, robust, and production-ready QA Automation Framework for the **Aqua Guard Android Application** and its corresponding web portal/Firebase console.

## Project Structure

```
automation/
    appium/                 # Appium Mobile Testing Module
        drivers/            # Emulator/device drivers
        pages/              # Page Object Model classes
        tests/              # Android functional regression tests (100 cases)
        utils/              # Mobile driver factory, test listeners, logs
        reports/            # Execution reports (Excel, HTML, JSON, XML, CSV)
            screenshots/    # Automatic failure screenshots
        logs/               # Log4j logs for Appium runs
    selenium/               # Selenium Web Testing Module
        pages/              # Web Page Object Model classes
        tests/              # Web Console & Dashboard regression tests (100 cases)
        utils/              # Web driver factory, report generation helpers
        reports/            # Execution reports (Excel, HTML, JSON, XML, CSV)
            screenshots/    # Automatic failure screenshots
    pom.xml                 # Maven dependency management config
    testng.xml              # Test execution suite mapping
    config.properties       # Automation configurations
    log4j2.xml              # Log4j logger configuration
```

## Technologies & Libraries

- **Language**: Java 17
- **Unit Test Runner**: TestNG (for XML suites, parameterization, and execution listeners)
- **Web Automation**: Selenium WebDriver 4
- **Mobile Automation**: Appium Java Client 9 (running Appium 2 Server)
- **Reporting**: Apache POI (Excel reports with charts), Custom HTML Dashboard, Jackson (JSON/XML reporting), OpenCSV (CSV reporting)
- **Logging**: Log4j 2

---

## Installation & Prerequisites

1. **Java Development Kit (JDK 17 or higher)** must be installed.
2. **Maven 3** must be installed and configured in your system environment path.
3. **Appium 2 Server** (for mobile tests):
   ```bash
   npm install -g appium
   appium driver install uiautomator2
   ```
4. **Android SDK & Emulator** set up locally (for local Appium runs).
5. **ChromeDriver** installed or managed automatically by Selenium 4 Manager.

---

## Execution Instructions

Configuration parameters can be modified inside `automation/config.properties`.

### 1. Running Selenium Web Tests (Locally)
To execute the web portal/Firebase console automation suite:
```bash
cd automation
mvn test -Dtest=AuthSeleniumTests,DashboardSeleniumTests,DeviceAdminSeleniumTests,ReportsSeleniumTests,FirebaseConsoleSeleniumTests
```

### 2. Running Appium Mobile Tests (Locally)
1. Launch your Android Emulator.
2. Start the Appium Server:
   ```bash
   appium
   ```
3. Run the Appium suite:
   ```bash
   cd automation
   mvn test -Dtest=AuthAppiumTests,DashboardAppiumTests,DeviceAppiumTests,MonitoringAppiumTests,SystemAppiumTests,PerformanceAppiumTests
   ```

### 3. Running All Tests Together
To run both suites using the TestNG suite runner:
```bash
cd automation
mvn clean test
```

---

## Execution Reports

Reports are automatically compiled on completion of a run and saved in the corresponding module's `reports/` folder:
- **HTML Dashboard Report (`TestReport.html`)**: Interactive dashboard with charts, statistics, execution times, and failure screenshot anchors.
- **Excel Report (`TestReport.xlsx`)**: Comprehensive test list including a dynamic pie chart indicating PASS/FAIL distribution.
- **Structured Data (`TestReport.json` / `TestReport.xml` / `TestReport.csv`)**: For CI/CD reporting and integration with third-party dashboards.
- **Failure Screenshots (`screenshots/`)**: Captured automatically whenever a test fails.

---

## GitHub Actions Workflows

Automated workflows are configured in `.github/workflows/` at the repository root:
1. **`selenium-tests.yml`**: Triggers on push/pull request to verify web console stability.
2. **`appium-tests.yml`**: Spins up a macOS virtual machine with hardware acceleration, builds the Android APK, starts an Android Emulator, starts the Appium Server, and runs Appium tests.
3. **`report.yml`**: Triggered on pushing to `main` branch. It executes both Selenium and Appium suites, aggregates Excel/HTML/CSV/JSON/XML report archives, builds a consolidated dashboard, and deploys it live to **GitHub Pages**.
4. **`web-e2e.yml` / `mobile-e2e.yml`**: Allows manual trigger of integration test runs.
