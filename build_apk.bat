@echo off
echo ===================================================
echo   Aqua Guard - Android APK Compiler
echo ===================================================
echo.

:: Create Gradle wrapper folders
if not exist "gradle\wrapper" (
    echo Creating Gradle Wrapper directories...
    mkdir gradle\wrapper
)

:: Download Gradle Wrapper Jar
echo Downloading Gradle Wrapper binary...
powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/gradle/gradle/v8.3.0/gradle/wrapper/gradle-wrapper.jar' -OutFile 'gradle\wrapper\gradle-wrapper.jar'"

:: Create gradle-wrapper.properties
echo Creating Gradle Wrapper properties...
(
echo distributionBase=GRADLE_USER_HOME
echo distributionPath=wrapper/dists
echo distributionUrl=https\://services.gradle.org/distributions/gradle-8.3-bin.zip
echo zipStoreBase=GRADLE_USER_HOME
echo zipStorePath=wrapper/dists
) > gradle\wrapper\gradle-wrapper.properties

:: Download gradlew scripts
echo Downloading compiler scripts...
powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/gradle/gradle/v8.3.0/gradlew' -OutFile 'gradlew'"
powershell -Command "Invoke-WebRequest -Uri 'https://raw.githubusercontent.com/gradle/gradle/v8.3.0/gradlew.bat' -OutFile 'gradlew.bat'"

:: Start compile build
echo.
echo Running compilation script. Please wait (this can take a few minutes for the first run)...
echo.
call gradlew.bat assembleDebug

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Build failed! Check compiler logs above.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo ===================================================
echo [SUCCESS] APK compiled successfully!
echo Path: app\build\outputs\apk\debug\app-debug.apk
echo ===================================================
pause
