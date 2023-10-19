package com.example.se;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.se.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_MIC_PERMISSION_REQUEST_CODE = 101;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestCameraAndMicrophonePermission();

    }

    private void requestCameraAndMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            // Permissions are already granted.
            // You can start your camera or microphone-related functionality here.
            Intent intent = new Intent(this,GetStarted.class);
            startActivity(intent);
        } else {
            // Request permissions.
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                    CAMERA_MIC_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted.
                // Start your camera or microphone-related functionality here.
//                Toast.makeText(this, "working", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, GetStarted.class);
                startActivity(intent);
            } else {
                // Permissions denied. Open a new activity or page.
                Toast.makeText(this, "Camera and microphone permissions denied.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, PermissionDenied.class);
                startActivity(intent);
            }
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == CAMERA_MIC_PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                // Permissions granted. You can start your camera or microphone-related functionality here.
//            } else {
//                // Permissions denied or "Ask every time" option selected.
//                // Handle the denial scenario here.
//                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
//                        shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
//                    // User selected "Ask every time."
//                    // You can provide additional information or guidance to the user.
//                } else {
//                    // User selected "Deny" or "Deny and don't ask again."
//                    // You may inform the user about the importance of these permissions.
//                }
//            }
//        }
//    }

}

