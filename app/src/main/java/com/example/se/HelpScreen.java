package com.example.se;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class HelpScreen extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;
    private String selectedVoiceName;
    private SpeechRecognizer speechRecognizer;
    private boolean isListening = false;
    private static final int SPEECH_REQUEST_CODE = 101;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_screen);
        Intent intent = getIntent();
        selectedVoiceName = intent.getStringExtra("selectedVoiceName");
        textToSpeech = new TextToSpeech(this, this);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Added the camera navigation function which interm calls the camera stream activity
                navigateToSecondPage();
            }
        });
//

    }
    @Override
    protected void onResume() {
//        Toast.makeText(GetStarted.this, "3", Toast.LENGTH_SHORT).show();
        super.onResume();
        speakInstructionsAndStartListening();
    }

    public void startListening() {
        // Start listening for voice input
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
                if (recognizedText.equals("next")) {
                    // User said "start"
                     next.setBackgroundColor(Color.parseColor("#FF0000"));
                    navigateToSecondPage();

                } else if (recognizedText.equals("back")) {
                            Intent intent = new Intent(this,VoiceSelection.class);
                            startActivity(intent);
                            finish();

                }
            }
        }
        // isListening = false;
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

    public void speakInstructionsAndStartListening() {
//    Vibration();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startListening();
            }
        }, 30000); // Adjust the delay time as needed (e.g., 5000 milliseconds for a 5-second delay)
    }
    private void Vibration(){
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    // Handle the case where vibration permission is not granted
                    // You might request the permission here
                }
            } else {
                vibrator.vibrate(100);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Release the SpeechRecognizer when the activity is destroyed
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
        Vibration();
        // Create a new intent for the Camera Module to start streaming.
        Intent intent = new Intent(this, CameraStream.class);
        startActivity(intent);
        finish();


    }
    private Voice getDesiredVoice(String selectedVoice) {
        if (selectedVoice.equals("men")) {
            // Assign the male voice using Voice
            return new Voice("hi-in-x-hie-local", new Locale("hi_IN"), 400, 200, false,  new HashSet<>());
        } else {
            // Assign the female voice using Voice
//            return new Voice("com.google.android.tts:en-in-x-ene-network", new Locale("en_IN"), 400, 200, false,  new HashSet<>());
//
            Voice defaultFemaleVoice = textToSpeech.getDefaultVoice();
            return defaultFemaleVoice;
        }
    }

    @Override
    public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                Voice selectedVoice = getDesiredVoice(selectedVoiceName);
                textToSpeech.setVoice(selectedVoice);
                textToSpeech.speak("This  help screen  is here to assist you in navigating the app.If you ever get stuck or have questions , refer to this guide for assistance by saying  Help  or clicking the  i  icon.    You can help use of voice assistance to navigate through the app by using basic navigation words like  'Next'  or  'Back'  or say button names read out at the beginning of every screen to access those screens. Please say 'Next' to navigate to the next screen", TextToSpeech.QUEUE_FLUSH, null);

            }
        }

    }
