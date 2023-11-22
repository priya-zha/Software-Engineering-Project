package com.example.se;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import java.util.Locale;

public class test extends AppCompatActivity {
    TextToSpeech speech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        speech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    for (Voice voice : speech.getVoices()) {
                        Log.d("Oninit_installed", voice.getName());
                    }
                }
//                    int result = speech.setLanguage(Locale.forLanguageTag("h1"));
//                    if(result == TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
//                        Log.d("oninit","language not insatlled");
//                        speech.setLanguage(Locale.forLanguageTag("en"));
//                    }
//                    else {
//                        speech.setLanguage(Locale.forLanguageTag("h1"));
//                        Voice voice =
//                        speech.setVoice()
//                    }
//                }
                else {
                    Log.d("oninit", "initilazed failed");
                }

            }
        });

    }
}