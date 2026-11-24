package com.example.mxqndisender;

import android.app.Activity;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Map;

public class MainActivity extends Activity {
    private TextView status;
    private TextView report;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(36, 24, 36, 24);
        root.setBackgroundColor(0xFF10151D);
        root.addView(text("MXQ NDI CAMERA SENDER", 28, 0xFFFFFFFF));
        status = text("MODE: HARDWARE DIAGNOSTIC / NDI BELUM AKTIF", 16, 0xFFFFC66D);
        root.addView(status);
        TextView intro = text("Aplikasi ini memeriksa USB host dan kemampuan encoder H.264 perangkat. Ini belum mengirim video NDI.", 17, 0xFFCFD8E3);
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(-1, -2); ip.topMargin = 16; ip.bottomMargin = 16; root.addView(intro, ip);
        Button scan = new Button(this); scan.setText("SCAN USB CAMERA + H.264"); scan.setTextSize(18); scan.setOnClickListener(v -> runScan()); root.addView(scan);
        ScrollView scroll = new ScrollView(this);
        report = text("Tekan SCAN untuk memulai pemeriksaan.\n", 16, 0xFFE8EDF4);
        scroll.addView(report);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, 0, 1); sp.topMargin = 16; root.addView(scroll, sp);
        setContentView(root);
        runScan();
    }

    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color); t.setPadding(4, 8, 4, 8); return t;
    }

    private void runScan() {
        StringBuilder b = new StringBuilder();
        UsbManager manager = (UsbManager)getSystemService(USB_SERVICE);
        Map<String, UsbDevice> devices = manager.getDeviceList();
        b.append("USB HOST\n");
        if (devices.isEmpty()) b.append("Tidak ada perangkat USB terdeteksi. Periksa OTG, kabel, daya, dan kamera UVC.\n");
        else {
            for (UsbDevice d : devices.values()) {
                b.append("• ").append(d.getDeviceName()).append("\n  VID:PID = ")
                 .append(String.format("%04X:%04X", d.getVendorId(), d.getProductId()))
                 .append(" | interfaces: ").append(d.getInterfaceCount()).append("\n");
                for (int i=0; i<d.getInterfaceCount(); i++) {
                    android.hardware.usb.UsbInterface in = d.getInterface(i);
                    b.append("  Interface ").append(i).append(" class=").append(in.getInterfaceClass())
                     .append(" subclass=").append(in.getInterfaceSubclass()).append("\n");
                }
            }
        }
        b.append("\nH.264 ENCODER\n");
        boolean found = false;
        try {
            MediaCodecList list = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
            for (MediaCodecInfo info : list.getCodecInfos()) {
                if (info.isEncoder()) {
                    for (String type : info.getSupportedTypes()) {
                        if ("video/avc".equalsIgnoreCase(type)) {
                            found = true;
                            b.append("• ").append(info.getName()).append("\n  hardwareAccelerated API: ")
                             .append(android.os.Build.VERSION.SDK_INT >= 29 ? info.isHardwareAccelerated() : "tidak tersedia di API 28")
                             .append("\n");
                            try {
                                MediaCodecInfo.VideoCapabilities vc = info.getCapabilitiesForType(type).getVideoCapabilities();
                                b.append("  max supported width/height: ").append(vc.getSupportedWidths().getUpper()).append(" x ")
                                 .append(vc.getSupportedHeights().getUpper()).append("\n");
                                b.append("  1920x1080 supported: ").append(vc.areSizeAndRateSupported(1920,1080,30)).append("\n");
                            } catch (Exception e) { b.append("  Detail capability tidak tersedia: ").append(e.getMessage()).append("\n"); }
                        }
                    }
                }
            }
        } catch (Exception e) { b.append("Gagal membaca MediaCodec: ").append(e.getMessage()).append("\n"); }
        if (!found) b.append("Tidak menemukan encoder H.264.\n");
        b.append("\nBUILD INFO\nAndroid: ").append(android.os.Build.VERSION.RELEASE)
         .append(" (SDK ").append(android.os.Build.VERSION.SDK_INT).append(")\nDevice: ")
         .append(android.os.Build.MANUFACTURER).append(" ").append(android.os.Build.MODEL)
         .append("\nABI: ").append(java.util.Arrays.toString(android.os.Build.SUPPORTED_ABIS));
        report.setText(b.toString());
        status.setText("SCAN SELESAI — HASIL DIAGNOSTIK, BUKAN STREAM NDI");
    }
}
