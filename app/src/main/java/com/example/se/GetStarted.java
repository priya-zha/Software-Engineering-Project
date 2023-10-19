package com.example.se;

import android.app.Activity;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.se.R;
import com.example.se.splash;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.Locale;

public class GetStarted extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private static final int SPEECH_REQUEST_CODE = 101;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private Button start;

    private TextView Texthint;

    private ImageView micButton;
    private boolean isListening = false;

    private Intent speechRecognizerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
        start = findViewById(R.id.start);
        Texthint = findViewById(R.id.text);
        micButton = findViewById(R.id.mic);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
       // textToSpeech = new TextToSpeech(this, this);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // Text-to-Speech initialization is successful
                    //setupUtteranceProgressListener();
                    Toast.makeText(GetStarted.this, "init", Toast.LENGTH_SHORT).show();
                    Log.e("TextToSpeech", "init");
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                        @Override
                        public void onStart(String utteranceId) {

                            Log.e("TextToSpeech", "on_start");
                            // Speech has started
                            // You can handle the start of speech here
                            //Toast.makeText(GetStarted.this, "on_start", Toast.LENGTH_SHORT).show();
                            //textToSpeech.speak("Hi, Welcome to the Visual Aid. Let us help you get started. Click on the 'Start' button or say 'Start' to initiate the process.", TextToSpeech.QUEUE_FLUSH, null);

                        }

                        @Override
                        public void onDone(String utteranceId) {
                            //Texthint.setText("Listening...");
                            // Speech has finished, start listening if not already listening
                           /* if (!isListening) {
                                Texthint.setText("Listening...");
                                speakInstructionsAndStartListening();
                            }*/
                            Log.e("TextToSpeech", "on_done"+utteranceId);
                            speakDone();
                            if (utteranceId.equals("uniqueId")) {
                                // This is the ID associated with your speech request
                                // Handle the start of speech here
                                Log.e("TextToSpeech", "on_done"+utteranceId);
                                speakDone();
                            } else {
                                // Handle other utterances if necessary
                            }

                        }

                        @Override
                        public void onError(String utteranceId) {
                            // Handle any errors
                            //Toast.makeText(GetStarted.this, "gg", Toast.LENGTH_SHORT).show();
                            Log.e("TextToSpeech", "Error occurred during synthesis");
                        }


                    });
                } else {
                    Toast.makeText(GetStarted.this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
                }
                speakOut();
            }
        });



        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000); // Set the silence length to 3 seconds (adjust as needed)

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            // Your RecognitionListener methods go here
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                Texthint.setText("Listening...");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                Texthint.setText("Tap again to speak");
                micButton.setImageResource(R.drawable.baseline_mic_off_24);
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                micButton.setImageResource(R.drawable.baseline_mic_off_24);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Texthint.setText(""+data.get(0));

                String text = data.get(0);
                Toast.makeText(GetStarted.this, "said"+text, Toast.LENGTH_SHORT).show();

                if (text.contains("start")) {
                    navigateToSecondPage();
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

//        start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                navigateToSecondPage();
//            }
//        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            navigateToSecondPage();
            }
        });

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    micButton.setImageResource(R.drawable.baseline_mic_24);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
       // speakInstructionsAndStartListening();
    }
    // Set up the UtteranceProgressListener
    private void setupUtteranceProgressListener() {

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {



            @Override

            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {


                if (utteranceId.equals("uniqueId")) {
                    // This is the ID associated with your speech request
                    // Handle the start of speech here
                    speakDone();
                } else {
                    // Handle other utterances if necessary
                }

                // Speech has finished, start listening if not already listening
                if (!isListening) {

                }
            }

            @Override
            public void onError(String utteranceId) {
                // Handle any errors
                Log.e("TextToSpeech", "Error occurred during synthesis");
            }


        });
        speakOut();
    }


    public void startListening() {
        // Start listening for voice input

        isListening = true;
        speechRecognizer.startListening(speechRecognizerIntent);

        /*Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);*/
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && !matches.isEmpty()) {
                String recognizedText = matches.get(0).toLowerCase();
                if (recognizedText.equals("start")) {
                    // User said "start"
                    navigateToSecondPage();
                }
            }
        }
       // isListening = false;
    }
*/

//    private void speakInstructions() {
//        // Use TextToSpeech to provide voice instructions.
//        isListening = false;
//        textToSpeech.speak("Hi, Welcome to the Visual Aid. Let us help you get started. Click on the 'Start' button or say 'Start' to initiate the process.", TextToSpeech.QUEUE_FLUSH, null);
//    }
public void speakInstructionsAndStartListening() {
    //textToSpeech.speak("Hi, Welcome to the Visual Aid. Let us help you get started. Click on the 'Start' button or say 'Start' to initiate the process.", TextToSpeech.QUEUE_FLUSH, null);
    //startListening();

    // Delay for a few seconds before starting speech recognition
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            Texthint.setText("Listening...");
            speechRecognizer.startListening(speechRecognizerIntent);
        }
    }, 5000); // Adjust the delay time as needed (e.g., 5000 milliseconds for a 5-second delay)
}

    public void speakOut() {
        Toast.makeText(GetStarted.this, "Text-to-Speech started", Toast.LENGTH_SHORT).show();

        //Texthint.setText("Listening...");
        textToSpeech.speak("Hi, Welcome to the Visual Aid. Let us help you get started. Click on the 'Start' button or say 'Start' to initiate the process.", TextToSpeech.QUEUE_FLUSH, null, "uniqueid");

    }

    public void speakDone() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GetStarted.this, "Text-to-Speech completed", Toast.LENGTH_SHORT).show();
                Texthint.setText("Listening...");
                micButton.setImageResource(R.drawable.baseline_mic_24);
                speechRecognizer.startListening(speechRecognizerIntent);
            }
        });
    }
    @Override
    protected void onDestroy() {
        // Release the TextToSpeech engine when the activity is destroyed.
        textToSpeech.stop();
        textToSpeech.shutdown();
        speechRecognizer.stopListening();
        speechRecognizer.destroy();
        super.onDestroy();
    }


    public void stopListening() {
        // Stop speech recognition if needed
        speechRecognizer.cancel();
        isListening = false;
    }
    private void navigateToSecondPage() {
        // Handle navigation to the second page here
        Intent intent = new Intent(this, VoiceSelection.class);
        startActivity(intent);
    }

    @Override
    public void onInit(int status) {

    }
}
