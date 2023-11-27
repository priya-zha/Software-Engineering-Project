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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

public class settings extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;
    private String selectedVoiceName;
    private RadioGroup voiceRadioGroup;
    private SpeechRecognizer speechRecognizer;
    private boolean isListening = false;
    private static final int SPEECH_REQUEST_CODE = 101;
    Button next;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 123;
    private ImageView voiceImageView;
    private boolean instructionsSpoken = false;
    private boolean voice = false;
    private int gender = 1;
    int number;

    String selectedVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        voiceRadioGroup = findViewById(R.id.radioGroupVoice);
            Intent intent = getIntent();
            selectedVoiceName = intent.getStringExtra("selectedVoiceName");
            textToSpeech = new TextToSpeech(this, this);
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//            next = findViewById(R.id.next);
//            next.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    navigateToSecondPage();
//                }
//            });
        voiceRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            selectedRadioButton.setBackgroundResource(R.drawable.radio_button);
            selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
            textToSpeech.setVoice(getDesiredVoice(selectedVoice));
            textToSpeech.speak("You have selected the " + selectedVoice + " voice.", TextToSpeech.QUEUE_FLUSH, null);
        });

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
     //   textToSpeech.speak("Let's us help you select the voice preference. You can select between a male voice and a female voice. Click on Male or Female option or just speak 'Male' to select the male voice, OR speak 'Female' to select a female voice so that you can hear sample voices and select the one according to your choice", TextToSpeech.QUEUE_FLUSH, null);
        PostDelayed(20000);
      //  textToSpeech.speak("Let's us help you select the voice preference. You can select between a male voice and a female voice. Click on Male or Female option or just speak 'Male' to select the male voice, OR speak 'Female' to select a female voice so that you can hear sample voices and select the one according to your choice", TextToSpeech.QUEUE_FLUSH, null);
        PostDelayed(40000);
    }

    public void PostDelayed(int a) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openVoiceRecognitionForPreference();
            }
        }, a);
    }

    public void stopListening() {
        if (isListening) {
            speechRecognizer.cancel();
            isListening = false;
        }
    }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
                ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0).toLowerCase();
                    if (recognizedText.equals("men") || recognizedText.equals("man") || recognizedText.equals("boy") || recognizedText.equals("male") || recognizedText.equals("gentlemen") || recognizedText.equals("gentleman")) {
                        Vibration();
                        RadioButton selectedRadioButton = findViewById(R.id.radioMales);
                        selectedRadioButton.setChecked(true);
                        //selectedRadioButton.setBackgroundResource(R.drawable.radio_button);
                        int redColor = Color.parseColor("#FF0000");
                        //selectedRadioButton.setTextColor(redColor);
                        selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
                        // Set the desired voice based on the selected voice
                        textToSpeech.setVoice(getDesiredVoice(selectedVoice));
                       // textToSpeech.speak("Male voice has been selected", TextToSpeech.QUEUE_FLUSH, null);
                        textToSpeech.speak("Male voice has been selected. Next do you want to change the text size of the app? If yes, please say small, medium or large. If you don't want to change the text size, please say 'No' so that we can navigate you to the camera page", TextToSpeech.QUEUE_FLUSH, null);

                    } else if (recognizedText.equals("woman") || recognizedText.equals("women") || recognizedText.equals("girl") || recognizedText.equals("female") || recognizedText.equals("lady") || recognizedText.equals("ladies")) {
                        Vibration();
                        RadioButton selectedRadioButton = findViewById(R.id.radioFemales);
                        selectedRadioButton.setChecked(true);
                      //  selectedRadioButton.setBackgroundResource(R.drawable.radio_button);
                        int redColor = Color.parseColor("#FF0000");
                      //  selectedRadioButton.setTextColor(redColor);
                        selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
                        textToSpeech.setVoice(getDesiredVoice(selectedVoice));
                      //  textToSpeech.speak("Female voice has been selected", TextToSpeech.QUEUE_FLUSH, null);
                        textToSpeech.speak("Female voice has been selected.  Do you want to change the text size of the app? If yes, please say small, medium or large. If you don't want to change the text size, please say 'No' so that we can navigate you to the camera page", TextToSpeech.QUEUE_FLUSH, null);


                    }
                    else if (recognizedText.equals("small")){
                        number =10;
                        FontSizeUtil.setDefaultTextSize(this, getWindow().getDecorView(), number);
                        textToSpeech.speak(" Small font size has been applied to the application. If you still wanna change the font size to medium or large, you can simply say Medium or Large. Remember the font size you select will be applied to the entire application. Or if you wanna leave this page and go to the camera page now, please say Navigate", TextToSpeech.QUEUE_FLUSH, null);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openVoiceRecognitionForPreference();
                            }
                        }, 20000);

                    }
                    else if (recognizedText.equals("medium")){
                        number=20;
                        FontSizeUtil.setDefaultTextSize(this, getWindow().getDecorView(), number);
                        textToSpeech.speak(" Medium font size has been applied to the application. If you still wanna change the font size to small or large, you can simply say small or Large. Remember the font size you select will be applied to the entire application. Or if you wanna leave this page and go to the camera page now, please say Navigate", TextToSpeech.QUEUE_FLUSH, null);
                      //  textToSpeech.speak("If you still wanna change the font size to small or large, you can simply say Small or Large.  Remember the font size you select will be applied to the entire application.  Or if you wanna go to the camera page now, please say Navigate", TextToSpeech.QUEUE_FLUSH, null);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openVoiceRecognitionForPreference();
                            }
                        }, 20000);
                    }
                    else if (recognizedText.equals("large")){
                        number=30;
                        FontSizeUtil.setDefaultTextSize(this, getWindow().getDecorView(), number);
                        textToSpeech.speak(" Large font size has been applied to the application. If you still wanna change the font size to small or medium, you can simply say small or medium. Remember the font size you select will be applied to the entire application. Or if you wanna leave this page and go to the camera page now, please say Navigate", TextToSpeech.QUEUE_FLUSH, null);
                       // textToSpeech.speak("If you still wanna change the font size to small or medium, you can simply say small or medium.  Remember the font size you select will be applied to the entire application. Or if you wanna go to the camera page now, please say Navigate", TextToSpeech.QUEUE_FLUSH, null);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openVoiceRecognitionForPreference();
                            }
                        }, 20000);
                    }
                    else if(recognizedText.equals("no") || recognizedText.equals("navigate")){
                        Intent intent = new Intent(this, MainActivity2.class);
                        intent.putExtra("selectedVoiceName", selectedVoice);
                        intent.putExtra("intValue", number);
                        intent.putExtra("intVal", 5);
                        startActivity(intent);
                        finish();

                    }

                    // isListening = false;
                }
            }
        }
                private Voice getDesiredVoice (String selectedVoice){
                    if (selectedVoice.equals("men")) {
                        // Assign the male voice using Voice
                        return new Voice("hi-in-x-hie-local", new Locale("hi_IN"), 400, 200, false, new HashSet<>());
                    } else {
                        Voice defaultFemaleVoice = textToSpeech.getDefaultVoice();
                        return defaultFemaleVoice;
                    }
                }

                @Override
                public void onInit ( int status){
                    if (status == TextToSpeech.SUCCESS) {
                        Voice selectedVoice = getDesiredVoice(selectedVoiceName);
                        textToSpeech.setVoice(selectedVoice);
                        //textToSpeech.speak("This help screen is here to assist you in navigating the app.If you ever get stuck or have questions , refer to this guide for assistance by saying  Help  or clicking the  i  icon.    You can help use of voice assistance to navigate through the app by using basic navigation words like  'Next'  or  'Back'  or say button names read out at the beginning of every screen to access those screens. Please say 'Next' to navigate to the next screen", TextToSpeech.QUEUE_FLUSH, null);
                        textToSpeech.speak("This is the settings accessibility page. With the help of this page you can change the voice as well as change the font size to small, medium or large for those people who find it difficult to view small texts. Okay. So starting with please select a male or a female voice if you want to", TextToSpeech.QUEUE_FLUSH, null);

                    }
                }
            }

