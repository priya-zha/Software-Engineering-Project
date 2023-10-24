package com.example.se;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.se.R;

import java.util.Locale;

public class Microphone extends AppCompatActivity implements OnInitListener {
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_microphone);

        textToSpeech = new TextToSpeech(this, this);
        speakInstructions();

        // Initiate voice instructions for microphone access.

        // Set touch event listener.
        View screenView = findViewById(R.id.screenLayout);
        screenView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                int screenWidth = v.getWidth();

                if (x < screenWidth / 2) {
                    // User tapped on the left side of the screen (allow).
                    // Implement the "allow" action here.
                    Toast.makeText(Microphone.this, "left", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Microphone.this,CameraPermission.class);
                    startActivity(intent);

                } else {
                    // User tapped on the right side of the screen (deny).
                    // Implement the "deny" action here.
                    Toast.makeText(Microphone.this, "Right", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Microphone.this, PermissionDenied.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Use TextToSpeech to provide voice instructions.
            Toast.makeText(this, "ok working", Toast.LENGTH_SHORT).show();
            textToSpeech.speak("Do you want to allow access to the microphone? Tap on the left part of the screen to allow, or tap on the right part of the screen to deny.", TextToSpeech.QUEUE_FLUSH, null);

        }
        else{
            Toast.makeText(this, "Not supported", Toast.LENGTH_SHORT).show();
        }
    }

    private void speakInstructions() {
        // Use TextToSpeech to provide voice instructions.
        textToSpeech.speak("Do you want to allow access to the microphone? Tap on the left part of the screen to allow, or tap on the right part of the screen to deny.", TextToSpeech.QUEUE_FLUSH, null);
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
