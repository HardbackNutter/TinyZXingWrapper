package com.hardbacknutter.tinyzxingwrapper.example;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;

import com.google.zxing.BarcodeFormat;

import java.util.List;

import com.hardbacknutter.tinyzxingwrapper.ScanContract;
import com.hardbacknutter.tinyzxingwrapper.ScanIntentResult;
import com.hardbacknutter.tinyzxingwrapper.ScanOptions;
import com.hardbacknutter.tinyzxingwrapper.example.databinding.ActivityMainBinding;
import com.hardbacknutter.tinyzxingwrapper.scanner.BarcodeFamily;

public class MainActivity
        extends AppCompatActivity {

    private ActivityMainBinding vb;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.isSuccess()) {
                    final String text = getString(R.string.msg_barcode,
                                                  result.getText(),
                                                  String.valueOf(result.getFormat()));
                    vb.lastScan.setText(text);

                } else {
                    final String reason = result.getFailure();
                    if (reason != null) {
                        switch (reason) {
                            case ScanIntentResult.Failure.REASON_MISSING_CAMERA_PERMISSION:
                                vb.lastScan.setText(R.string.err_permission);
                                break;
                            case ScanIntentResult.Failure.REASON_TIMEOUT:
                                vb.lastScan.setText(R.string.err_hard_timeout);
                                break;
                            case ScanIntentResult.Failure.REASON_INACTIVITY:
                                vb.lastScan.setText(R.string.err_inactivity);
                                break;
                            default:
                                vb.lastScan.setText(getString(R.string.err_unknown, reason));
                                break;
                        }
                    } else {
                        vb.lastScan.setText(R.string.err_cancelled);
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        // All insets rely on android:fitsSystemWindows="true"
        // as set on the top CoordinatorLayout.
        // The status-bar will be transparent.
        // Not the "best" look, but more then good enough for this app

        // EdgeToEdge on Android pre-15; but only starting Android 11 up
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            EdgeToEdge.enable(this);
        }

        super.onCreate(savedInstanceState);

        vb = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(vb.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        vb.btnScanGeneric.setOnClickListener(this::scanGeneric);
        vb.btnScanGenericUsingFrontCamera.setOnClickListener(this::scanGenericFrontCamera);
        vb.btnScanProduct.setOnClickListener(this::scanProduct);
        vb.btnScanQrCode.setOnClickListener(this::scanQr);
        vb.btnScanDataMatrix.setOnClickListener(this::scanDataMatrix);
    }

    private void scanGeneric(@NonNull final View view) {
        barcodeLauncher.launch(new ScanOptions());
    }

    private void scanGenericFrontCamera(@NonNull final View view) {
        final ScanOptions options = new ScanOptions()
                .setPrompt(getString(R.string.msg_scan_prompt))
                .setUseCameraWithLensFacing(CameraSelector.LENS_FACING_FRONT);
        barcodeLauncher.launch(options);
    }

    private void scanProduct(@NonNull final View view) {
        final ScanOptions options = new ScanOptions()
                .setBarcodeFormats(BarcodeFamily.PRODUCT);
        barcodeLauncher.launch(options);
    }

    private void scanQr(@NonNull final View view) {
        final ScanOptions options = new ScanOptions()
                .setBarcodeFormats(List.of(BarcodeFormat.QR_CODE));
        barcodeLauncher.launch(options);
    }

    private void scanDataMatrix(@NonNull final View view) {
        final ScanOptions options = new ScanOptions()
                .setBarcodeFormats(List.of(BarcodeFormat.DATA_MATRIX));
        barcodeLauncher.launch(options);
    }
}
