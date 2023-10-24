package com.example.se;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

public class PermissionDenied extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;
    private boolean isTTSInitialized = false;

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
        builder.setMessage("Do you want to go back to the previous page or exit the app?");
        builder.setPositiveButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle "Back" button press.
                // In this example, we just finish the current activity.
                finish();
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