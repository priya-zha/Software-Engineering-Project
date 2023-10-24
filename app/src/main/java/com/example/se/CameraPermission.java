package com.example.se;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;

public class CameraPermission extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_permission);
        if (checkCameraPermission()) {
            // Camera permission is granted; you can now use the camera.
        }

    }
    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted; request it.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted.
                Intent intent = new Intent(this, MainScreen.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this,PermissionDenied.class);
                startActivity(intent);
                // Camera permission is denied.
            }
        }
    }
//        private void showBackExitDialog() {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Confirmation");
//            builder.setMessage("Do you want to go back to the previous page or exit the app?");
//            builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    // Handle "Back" button press.
//                    // In this example, we just finish the current activity.
//                    finish();
//                }
//            });
//            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    // Handle "Exit" button press.
//                    // In this example, we exit the app.
//                    finishAffinity(); // Close all activities in the task.
//                }
//            });
//
//            AlertDialog dialog = builder.create();
//            dialog.show();
//    }
}