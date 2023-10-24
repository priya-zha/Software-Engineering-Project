package com.example.se;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class VoiceSelection extends Activity implements TextToSpeech.OnInitListener {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 123;
    private RadioGroup voiceRadioGroup;
    private ImageView voiceImageView;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private boolean instructionsSpoken = false;
    private boolean isListening = false;
    private boolean voice = false;
    private int gender = 1;
    String selectedVoiceName;
    String selectedVoice;

    public int getGender() {
        return gender;
    }

    public void setGender(int newGender) {
        gender = newGender;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_selection);
        voiceRadioGroup = findViewById(R.id.voiceRadioGroup);
        voiceImageView = findViewById(R.id.voiceImageView);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        textToSpeech = new TextToSpeech(this, this);

        voiceRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            selectedRadioButton.setBackgroundResource(R.drawable.radio_button);
            selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
            textToSpeech.setVoice(getDesiredVoice(selectedVoice));
            textToSpeech.speak("You have selected the " + selectedVoice + " voice.", TextToSpeech.QUEUE_FLUSH, null);
            // Update the ImageView based on the selected voice
            int imageResource = (selectedVoice.equals("men")) ? R.drawable.male_image : R.drawable.female_image;
            voiceImageView.setImageResource(imageResource);
        });

        // Select button functionality
        Button selectButton = findViewById(R.id.selectButton);
        selectButton.setOnClickListener(v -> {
            // Prompt the user for confirmation or selection
            Intent nextActivityIntent = new Intent(VoiceSelection.this, HelpScreen.class);
            nextActivityIntent.putExtra("selectedVoiceName", selectedVoice);
            // You can add more properties here if needed
            startActivity(nextActivityIntent);
            textToSpeech.speak("Voice has been selected", TextToSpeech.QUEUE_FLUSH, null);
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
        textToSpeech.speak("Let's us help you select the voice preference. You can select between a male voice and a female voice. Click on Male or Female option or just speak 'Male' to select the male voice, OR speak 'Female' to select a female voice so that you can hear sample voices and select the one according to your choice", TextToSpeech.QUEUE_FLUSH, null);
        PostDelayed(15000);
        PostDelayed(30000);
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

    // Handle voice recognition results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && !matches.isEmpty()) {
                String recognizedText = matches.get(0).toLowerCase();
                if (recognizedText.equals("men") || recognizedText.equals("man") || recognizedText.equals("boy") || recognizedText.equals("male") || recognizedText.equals("gentlemen") || recognizedText.equals("gentleman")) {
                    Vibration();
                    setGender(1);
                    RadioButton selectedRadioButton = findViewById(R.id.radioMale);
                    selectedRadioButton.setChecked(true);
                    selectedRadioButton.setBackgroundResource(R.drawable.radio_button);
                    String selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
                    // Set the desired voice based on the selected voice
                    textToSpeech.setVoice(getDesiredVoice(selectedVoice));
                    textToSpeech.speak("This is a male sample voice. Do you want to select this voice? If yes, please say 'Select'. If no, please say 'No' so that we can play the female sample voice", TextToSpeech.QUEUE_FLUSH, null);
                } else if (recognizedText.equals("woman") || recognizedText.equals("women") || recognizedText.equals("girl") || recognizedText.equals("female") || recognizedText.equals("lady") || recognizedText.equals("ladies")) {
                    Vibration();
                    setGender(2);
                    RadioButton selectedRadioButton = findViewById(R.id.radioFemale);
                    selectedRadioButton.setChecked(true);
                    selectedRadioButton.setBackgroundResource(R.drawable.radio_button);
                    String selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
                    textToSpeech.setVoice(getDesiredVoice(selectedVoice));
                    textToSpeech.speak("This is a female sample voice. Do you want to select this voice? If yes, please say 'Select'. If no, please say 'No' so that we can play the male sample voice", TextToSpeech.QUEUE_FLUSH, null);
                } else if (recognizedText.equals("previous")) {
                    switch (getGender()) {
                        case 2:
                            Vibration();
                            Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
                            RadioButton selected = findViewById(R.id.radioMale);
                            selected.setChecked(true);
                            selected.setBackgroundResource(R.drawable.radio_button);
                            String voice = selected.getText().toString().toLowerCase();
                            // Set the desired voice based on the selected voice
                            textToSpeech.setVoice(getDesiredVoice(voice));
                            Button selectButton = findViewById(R.id.selectButton);
                            selectButton.setBackgroundColor(Color.parseColor("#FF0000"));
                            textToSpeech.speak("Male voice has been selected for you", TextToSpeech.QUEUE_FLUSH, null);
                            selectedVoiceName = "men";
                            Intent nextActivityIntent = new Intent(VoiceSelection.this, HelpScreen.class);
                            nextActivityIntent.putExtra("selectedVoiceName", selectedVoiceName);
                            // You can add more properties here if needed
                            startActivity(nextActivityIntent);
                            break;
                        case 1:
                            Vibration();
                            Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
                            RadioButton selectedFemale = findViewById(R.id.radioFemale);
                            selectedFemale.setChecked(true);
                            selectedFemale.setBackgroundResource(R.drawable.radio_button);
                            String femaleVoice = selectedFemale.getText().toString().toLowerCase();
                            // Set the desired voice based on the selected voice
                            textToSpeech.setVoice(getDesiredVoice(femaleVoice));
                            Button selectButtonFemale = findViewById(R.id.selectButton);
                            selectButtonFemale.setBackgroundColor(Color.parseColor("#FF0000"));
                            textToSpeech.speak("Female voice has been selected for you", TextToSpeech.QUEUE_FLUSH, null);
                            selectedVoiceName = "women";
                            Intent nextActivityIntentFemale = new Intent(VoiceSelection.this, HelpScreen.class);
                            nextActivityIntentFemale.putExtra("selectedVoiceName", selectedVoiceName);
                            // You can add more properties here if needed
                            startActivity(nextActivityIntentFemale);
                            break;
                    }
                } else if (recognizedText.equals("no")) {
                    switch (getGender()) {
                        case 1:
                            Vibration();
                            setGender(2);
                            RadioButton selectedRadioButton = findViewById(R.id.radioFemale);
                            selectedRadioButton.setChecked(true);
                            selectedRadioButton.setBackgroundResource(R.drawable.radio_button);
                            String selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
                            textToSpeech.setVoice(getDesiredVoice(selectedVoice));
                            textToSpeech.speak("This is a female sample voice. Do you want to select this voice? If yes, please say 'Select'. If no, please say 'previous' so that we can select the male voice for you", TextToSpeech.QUEUE_FLUSH, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    openVoiceRecognitionForPreference();
                                }
                            }, 15000);
                            break;
                        case 2:
                            Vibration();
                            setGender(1);
                            RadioButton selectedRadioButtonMale = findViewById(R.id.radioMale);
                            selectedRadioButtonMale.setChecked(true);
                            selectedRadioButtonMale.setBackgroundResource(R.drawable.radio_button);
                            String selectedVoiceMale = selectedRadioButtonMale.getText().toString().toLowerCase();
                            textToSpeech.setVoice(getDesiredVoice(selectedVoiceMale));
                            textToSpeech.speak("This is a male sample voice. Do you want to select this voice? If yes, please say 'Select'. If no, please say 'previous' so that we can select a female voice for you", TextToSpeech.QUEUE_FLUSH, null);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    openVoiceRecognitionForPreference();
                                }
                            }, 15000);
                            break;
                    }
                } else if (recognizedText.equals("select")) {
                    if (getGender() == 1) {
                        Vibration();
                        selectedVoiceName = "men";
                        Button selectButton = findViewById(R.id.selectButton);
                        selectButton.setBackgroundColor(Color.parseColor("#FF0000"));
                        textToSpeech.setVoice(getDesiredVoice("men"));
                        textToSpeech.speak("Men voice has been selected", TextToSpeech.QUEUE_FLUSH, null);
                    } else if (getGender() == 2) {
                        Vibration();
                        selectedVoiceName = "women";
                        Button selectButton = findViewById(R.id.selectButton);
                        selectButton.setBackgroundColor(Color.parseColor("#FF0000"));
                        textToSpeech.setVoice(getDesiredVoice("women"));
                        textToSpeech.speak("Female voice has been selected", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    Intent nextActivityIntent = new Intent(VoiceSelection.this, HelpScreen.class);
                    nextActivityIntent.putExtra("selectedVoiceName", selectedVoiceName);
                    // You can add more properties here if needed
                    startActivity(nextActivityIntent);
                } else if (recognizedText.equals("back") || recognizedText.equals("go back")) {
                    Intent intent = new Intent(this, GetStarted.class);
                    startActivity(intent);
                    finish();
                } else {
                    textToSpeech.speak("Invalid input", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }
    }

    private void playSampleVoice() {
        // Get the selected radio button
        RadioButton selectedRadioButton = findViewById(voiceRadioGroup.getCheckedRadioButtonId());
        // Get the text of the selected radio button
        String selectedVoice = selectedRadioButton.getText().toString().toLowerCase();
        // Set the desired voice based on the selected voice
        textToSpeech.setVoice(getDesiredVoice(selectedVoice));
        // Define the sample text
        String sampleText = (selectedVoice.equals("men")) ? "This is a sample male voice." : "This is a sample female voice.";
        // Speak the sample text with the specified voice
        HashMap<String, String> params = new HashMap<>();
        String utteranceId = (selectedVoice.equals("men")) ? "maleVoice" : "femaleVoice";
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
        textToSpeech.speak(sampleText, TextToSpeech.QUEUE_FLUSH, params);
    }

    // Get the desired voice based on the selected voice
    private Voice getDesiredVoice(String selectedVoice) {
        if (selectedVoice.equals("men")) {
            // Assign the male voice using Voice
            return new Voice("hi-in-x-hie-local", new Locale("hi_IN"), 400, 200, false, new HashSet<>());
        } else {
            Voice defaultFemaleVoice = textToSpeech.getDefaultVoice();
            return defaultFemaleVoice;
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Text-to-Speech initialization is successful
            textToSpeech.speak("Let's us help you select the voice preference. You can say 'men' to hear a male sample voice and you can say 'women' to hear a sample female voice", TextToSpeech.QUEUE_FLUSH, null);
        } else {
            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
        }
    }
}
