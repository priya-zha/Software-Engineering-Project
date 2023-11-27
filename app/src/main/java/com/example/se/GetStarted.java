package com.example.se;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.se.R;
import com.example.se.splash;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

public class GetStarted extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private static final int SPEECH_REQUEST_CODE = 101;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private Button start;
    private boolean instructionsSpoken = false;
    private boolean isListening = false;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        start = findViewById(R.id.start);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        textToSpeech = new TextToSpeech(this, this);
        instructionsSpoken = false;
        imageView = findViewById(R.id.imageView);

        Glide.with(this)
                .load(R.drawable.mic)  // Replace with the actual resource ID of your GIF image
                .into(imageView);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSecondPage();
            }
        });

    }

    //    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!instructionsSpoken) {
//            speakInstructionsAndStartListening(30000);
//            instructionsSpoken = true; // Set the flag to true after speaking the instructions
//        }
//    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!instructionsSpoken) {
            speakInstructionsAndStartListening(30000);
            instructionsSpoken = true; // Set the flag to true after speaking the instructions
        }
    }

    public void startListening() {
        Vibration();
        isListening = true;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && !matches.isEmpty()) {
                String recognizedText = matches.get(0).toLowerCase();
                if (recognizedText.equals("start")) {
                    // User said "start"
                    start.setBackgroundColor(Color.parseColor("#FF0000"));
                    navigateToSecondPage();
                }
                else{

                    textToSpeech.speak("Sorry. I didn't get you. Can you please say start to move forward?", TextToSpeech.QUEUE_FLUSH, null);
                    speakInstructionsAndStartListening(5000);
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        // Handle the back button press
        super.onBackPressed();
        moveTaskToBack(true); // Minimize the app or bring it to the background
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Stop text-to-speech when the activity goes into the background
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        if (speechRecognizer != null) {
            speechRecognizer.cancel();
            speechRecognizer.destroy();
        }
    }
    public void speakInstructionsAndStartListening(int a) {
        Vibration();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startListening();
            }
        }, a); // Adjust the delay time as needed (e.g., 5000 milliseconds for a 5-second delay)
    }
    private void Vibration(){
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                }
            } else {
                vibrator.vibrate(100);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopListening(); // Stop it here

        if (speechRecognizer != null) {
            speechRecognizer.cancel();
            speechRecognizer.destroy();
        }
    }

    public void stopListening() {
        // Stop speech recognition if needed
//        speechRecognizer.cancel();
//        isListening = false;
        if (isListening) {
            speechRecognizer.cancel();
            isListening = false;
        }
    }
    private void navigateToSecondPage() {
        stopListening();
        // Handle navigation to the second page here
        Intent intent = new Intent(this, VoiceSelection.class);
        startActivity(intent);
        finish();

    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Text-to-Speech initialization is successful
            textToSpeech.speak("Hi, Welcome to the Visual Aid. Let us help you get started. The app uses vibrations to indicate different app states like idle, listening,generating text, and such say instructions to feel the difference in vibration from listening and when the app is generating text. You can say 'Start' when you hear a notification sound and feel the vibration to initiate the process.", TextToSpeech.QUEUE_FLUSH, null);

        } else {
            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
        }
    }
}
