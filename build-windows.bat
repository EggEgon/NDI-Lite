@echo off
setlocal
cd /d "%~dp0"
echo ================================================
echo MXQ NDI Camera Sender - Windows APK build
echo ================================================
where java >nul 2>nul || (echo ERROR: JDK 17 belum terpasang. & echo Install JDK 17 lalu buka terminal baru. & pause & exit /b 1)
where gradle >nul 2>nul || (echo ERROR: Gradle belum terpasang atau belum ada di PATH. & echo Install Gradle 8.9 atau lebih baru yang kompatibel dengan AGP 8.7.3. & pause & exit /b 1)
if "%ANDROID_HOME%"=="" if "%ANDROID_SDK_ROOT%"=="" (echo ERROR: Android SDK belum diset. Set ANDROID_HOME ke folder Android SDK. & pause & exit /b 1)
call gradle --version
if errorlevel 1 exit /b 1
call gradle --no-daemon assembleDebug
if errorlevel 1 (echo BUILD GAGAL. Periksa pesan error di atas. & pause & exit /b 1)
echo.
echo BUILD BERHASIL:
echo app\build\outputs\apk\debug\app-debug.apk
pause
