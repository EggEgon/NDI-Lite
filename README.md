# MXQ NDI Camera Sender — Windows build starter

## Status
This repository currently builds a **diagnostic APK**, not a working NDI sender. It scans USB devices and reports available H.264 MediaCodec encoders/capabilities. It does not yet capture UVC frames, encode camera frames, or transmit NDI.

Real NDI HX transmission requires the appropriate NDI SDK/license and native Android libraries. USB camera capture also needs a UVC implementation compatible with the MXQ firmware.

## Build without Android Studio
GitHub Actions builds the diagnostic APK in the cloud:
1. Open the **Actions** tab.
2. Select **Build diagnostic APK**.
3. Run workflow, or push a commit to main.
4. Download artifact **mxq-ndi-diagnostic-apk** when the run succeeds.

## Test on MXQ
Install the APK, connect the USB camera, open the app, and press **SCAN USB CAMERA + H.264**. Send the complete report back so device compatibility can be checked.

## Next steps
1. Confirm MXQ SoC/firmware and USB camera VID/PID + formats.
2. Add UVC capture and verify live preview.
3. Feed camera frames to MediaCodec H.264 and measure actual 1080p30 performance.
4. Integrate licensed NDI HX Android SDK/native transport and verify reception in OBS + DistroAV.

## Windows local build
See `build-windows.bat`. Requires JDK 17, Android SDK command-line tools, and Gradle 8.9; Android Studio is not required.
