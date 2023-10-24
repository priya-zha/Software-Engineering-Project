package com.example.se;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

public class PermissionDenied extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;
    private boolean isTTSInitialized = false;

    private static final int PERMISSION_SETTINGS_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_denied);
        textToSpeech = new TextToSpeech(this,this);
        showBackExitDialog();

    }

    private void showBackExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("App needs access to the camera and microphone to function. Please enable these permissions in the app settings.");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle "Back" button press.
                // In this example, we just finish the current activity.
                openAppSettings();
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle "Exit" button press.
                // In this example, we exit the app.
                finishAffinity(); // Close all activities in the task.
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, PERMISSION_SETTINGS_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, "request code"+requestCode, Toast.LENGTH_SHORT).show();

        if (requestCode == PERMISSION_SETTINGS_REQUEST_CODE) {
            // Check if the user has granted the permissions.
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted. Start the GetStarted activity.
                Intent intent = new Intent(this, GetStarted.class);
                startActivity(intent);
            } else {
                // Permissions still not granted.
                // You can handle this case as needed, such as showing a message or taking appropriate action.
                Intent intent = new Intent(this, PermissionDenied.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Use TextToSpeech to provide voice instructions.
            Toast.makeText(this, "ok working", Toast.LENGTH_SHORT).show();
            textToSpeech.speak("Do you want to go back to the previous page or exit the application? To go back, say 'Back' and to exit the application say 'Exit'", TextToSpeech.QUEUE_FLUSH, null);

        }
        else{
            Toast.makeText(this, "Not supported", Toast.LENGTH_SHORT).show();
        }
    }

    private void speakInstructions() {
        // Use TextToSpeech to provide voice instructions.
        textToSpeech.speak("Do you want to go back to the previous page or exit the application? To go back, say 'Back' and to exit the application say 'Exit'", TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        speakInstructions();
    }

    @Override
    protected void onDestroy() {
        // Release the TextToSpeech engine when the activity is destroyed.
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}