@echo off
echo ===================================================
echo   Aqua Guard - Expo Go Wrapper Starter
echo ===================================================
echo.
cd expo_wrapper
echo Installing dependencies (this may take a minute on first run)...
call npm install
echo.
echo Starting Expo Development Server...
echo.
echo IMPORTANT: Make sure your computer and phone are connected to the SAME Wi-Fi network!
echo.
call npx expo start
pause
