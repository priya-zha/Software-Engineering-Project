package com.example.se;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class CameraPermission extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private TextToSpeech textToSpeech;
    private String selectedVoiceName;
    private SpeechRecognizer speechRecognizer;
    private boolean isListening = false;
    private static final int SPEECH_REQUEST_CODE = 101;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 123;
    private boolean instructionsSpoken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_permission);
        Intent intent = getIntent();
        selectedVoiceName = intent.getStringExtra("selectedVoiceName");
        textToSpeech = new TextToSpeech(this, this);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        if (checkCameraPermission()) {
            // Camera permission is granted; you can now use the camera.
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!instructionsSpoken) {
            speakInstructionsAndStartListening();
            instructionsSpoken = true; // Set the flag to true after speaking the instructions
        }
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

                } else if (recognizedText.equals("back")) {
                    Intent intent = new Intent(this,HelpScreen.class);
                    startActivity(intent);
                    finish();

                }
            }
        }
        // isListening = false;
    }
    private void Vibration() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            } else {
                vibrator.vibrate(100);
            }
        }
    }
    private void openVoiceRecognitionForPreference() {
        Vibration();
        isListening = true;
        Intent voiceRecognitionIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceRecognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(voiceRecognitionIntent, VOICE_RECOGNITION_REQUEST_CODE);
    }
    public void speakInstructionsAndStartListening() {
        Vibration();
        textToSpeech.speak("Let's us help you select the voice preference. You can select between a male voice and a female voice. Click on Male or Female option or just speak 'Male' to select the male voice, OR speak 'Female' to select a female voice so that you can hear sample voices and select the one according to your choice", TextToSpeech.QUEUE_FLUSH, null);
        PostDelayed(15000);

    }
    public void PostDelayed(int a) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openVoiceRecognitionForPreference();
            }
        }, a);
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
            textToSpeech.speak("This is a camera screen. You have a camera button on the top part of the screen. We require your permission to open the camera app and capture the image. Do you agree? If yes, please say 'Yes', else please say 'No' ", TextToSpeech.QUEUE_FLUSH, null);

            // textToSpeech.speak("This  help screen  is here to assist you in navigating the app.If you ever get stuck or have questions , refer to this guide for assistance by saying  Help  or clicking the  i  icon.    You can help use of voice assistance to navigate through the app by using basic navigation words like  'Next'  or  'Back'  or say button names read out at the beginning of every screen to access those screens. Please say 'Next' to navigate to the next screen", TextToSpeech.QUEUE_FLUSH, null);

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