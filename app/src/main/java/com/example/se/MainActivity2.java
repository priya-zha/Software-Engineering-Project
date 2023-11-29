//package com.example.se;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.VibrationEffect;
//import android.os.Vibrator;
//import android.provider.MediaStore;
//import android.provider.Settings;
//import android.speech.RecognizerIntent;
//import android.speech.SpeechRecognizer;
//import android.speech.tts.TextToSpeech;
//import android.speech.tts.Voice;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.util.Log;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.content.ContextCompat;
//
//import com.example.se.APIInterface;
//
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Locale;
//
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class MainActivity2 extends AppCompatActivity implements TextToSpeech.OnInitListener {
//    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
//    private TextToSpeech textToSpeech;
//    private String selectedVoiceName;
//    private SpeechRecognizer speechRecognizer;
//    private boolean isListening = false;
//    private static final int SPEECH_REQUEST_CODE = 101;
//    private static final int VOICE_RECOGNITION_REQUEST_CODE = 123;
//    private boolean instructionsSpoken = false;
//    TextView responseText;
//    APIInterface apiInterface;
//    // Define the pic id
//    private static final int pic_id = 123;
//    // Define the button and imageview type variable
//    Button camera_open_id;
//    ImageView click_image_id;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main2);
//        apiInterface = APIClient.getClient().create(APIInterface.class);
//
//        // By ID we can get each component which id is assigned in XML file get Buttons and imageview.
//        camera_open_id = findViewById(R.id.camera_button);
//        click_image_id = findViewById(R.id.click_image);
//        responseText = findViewById(R.id.responseText);
//        Intent intent = getIntent();
//        selectedVoiceName = intent.getStringExtra("selectedVoiceName");
//        textToSpeech = new TextToSpeech(this, this);
//        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
//        if (Build.VERSION.SDK_INT >= 30){
//            if (!Environment.isExternalStorageManager()){
//                Intent getpermission = new Intent();
//                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivity(getpermission);
//            }
//        }
//
//        // Camera_open button is for opening the camera and adding the setOnClickListener in this button
//        camera_open_id.setOnClickListener(v -> {
//            // Create the camera_intent ACTION_IMAGE_CAPTURE, it will open the camera for capturing the image
//            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            // Start the activity with camera_intent and request pic id
//            startActivityForResult(camera_intent, pic_id);
//        });
//
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!instructionsSpoken) {
//            speakInstructionsAndStartListening();
//            instructionsSpoken = true; // Set the flag to true after speaking the instructions
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        // Stop text-to-speech when the activity goes into the background
//        if (textToSpeech != null) {
//            textToSpeech.stop();
//        }
//        if (speechRecognizer != null) {
//            speechRecognizer.cancel();
//            speechRecognizer.destroy();
//        }
//    }
//
//    public void startListening() {
//        // Start listening for voice input
//        Vibration();
//        isListening = true;
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        startActivityForResult(intent, SPEECH_REQUEST_CODE);
//    }
//    private void Vibration() {
//        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        if (vibrator != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
//                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
//                }
//            } else {
//                vibrator.vibrate(100);
//            }
//        }
//    }
//    private void openVoiceRecognitionForPreference() {
//        Vibration();
//        isListening = true;
//        Intent voiceRecognitionIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        voiceRecognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        startActivityForResult(voiceRecognitionIntent, VOICE_RECOGNITION_REQUEST_CODE);
//    }
//    public void speakInstructionsAndStartListening() {
//        Vibration();
//        textToSpeech.speak("Let's us help you select the voice preference. You can select between a male voice and a female voice. Click on Male or Female option or just speak 'Male' to select the male voice, OR speak 'Female' to select a female voice so that you can hear sample voices and select the one according to your choice", TextToSpeech.QUEUE_FLUSH, null);
//        PostDelayed(15000);
//
//    }
//    public void PostDelayed(int a) {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                openVoiceRecognitionForPreference();
//            }
//        }, a);
//    }
//
//    // This method will help to retrieve the image
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // Match the request 'pic id with requestCode
////        if (requestCode == pic_id && resultCode == RESULT_OK) {
////            // BitMap is a data structure of the image file which stores the image in memory
////            Bitmap photo = (Bitmap) data.getExtras().get("data");
////            // Set the image in the ImageView for display
////            click_image_id.setImageBitmap(photo);
////
////            // Send the captured image to the Flask API
////            sendImageToFlask(photo);
////        }
//        if (requestCode == pic_id && resultCode == RESULT_OK) {
//            // BitMap is a data structure of the image file which stores the image in memory
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            // Set the image in the ImageView for display
//            click_image_id.setImageBitmap(photo);
//
//            // Send the captured image to the Flask API
//            sendImageToFlask(photo);
//
//        } else if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
//            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//            if (matches != null && !matches.isEmpty()) {
//                String recognizedText = matches.get(0).toLowerCase();
//                if (recognizedText.equals("next")) {
//                    // User said "start"
//                } else if (recognizedText.equals("back")) {
//                    Intent intent = new Intent(this, HelpScreen.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        }
//    }
//
//
//    // Method to send the captured image to the Flask API
//    private void sendImageToFlask(Bitmap photo) {
//        // Convert the Bitmap to a byte array
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//
//        // Create a RequestBody for the image data
//        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), byteArray);
//
//        // Create a MultipartBody.Part with the image data
//        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.png", requestFile);
//
//        // Make the API call to process the image
//        Call<ResponseBody> call = apiInterface.processImage(body);
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    // Handle the response, which will contain the annotated image
//                    // You can extract the image data from the response body
//
//                    ResponseBody responseBody = response.body();
//                    InputStream inputStream = responseBody.byteStream();
//
//                    // Process the InputStream or display the image as needed
//                    // For example, you can convert the InputStream to a Bitmap
//
//                    Bitmap annotatedImage = BitmapFactory.decodeStream(inputStream);
//
//                    // Update your UI with the annotated image
//                    click_image_id.setImageBitmap(annotatedImage);
//                } else {
//                    // Handle the error
//                    Toast.makeText(MainActivity2.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                // Handle the failure
//                Toast.makeText(MainActivity2.this, "Failed to send the image: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    private Voice getDesiredVoice(String selectedVoice) {
//        if (selectedVoice.equals("men")) {
//            // Assign the male voice using Voice
//            return new Voice("hi-in-x-hie-local", new Locale("hi_IN"), 400, 200, false,  new HashSet<>());
//        } else {
//            // Assign the female voice using Voice
////            return new Voice("com.google.android.tts:en-in-x-ene-network", new Locale("en_IN"), 400, 200, false,  new HashSet<>());
////
//            Voice defaultFemaleVoice = textToSpeech.getDefaultVoice();
//            return defaultFemaleVoice;
//        }
//    }
//    @Override
//    public void onInit(int status) {
//        if (status == TextToSpeech.SUCCESS) {
//            Voice selectedVoice = getDesiredVoice(selectedVoiceName);
//            textToSpeech.setVoice(selectedVoice);
//            textToSpeech.speak("This is a camera screen. You have a camera button on the top part of the screen. We require your permission to open the camera app and capture the image. Do you agree? If yes, please say 'Yes', else please say 'No' ", TextToSpeech.QUEUE_FLUSH, null);
//
//            // textToSpeech.speak("This  help screen  is here to assist you in navigating the app.If you ever get stuck or have questions , refer to this guide for assistance by saying  Help  or clicking the  i  icon.    You can help use of voice assistance to navigate through the app by using basic navigation words like  'Next'  or  'Back'  or say button names read out at the beginning of every screen to access those screens. Please say 'Next' to navigate to the next screen", TextToSpeech.QUEUE_FLUSH, null);
//
//        }
//    }
//}
package com.example.se;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.se.APIInterface;
//import com.example.se.pojo.MultipleResource.ImageResponse;
import com.example.se.pojo.ImageResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.Manifest;

public class MainActivity2 extends AppCompatActivity implements TextToSpeech.OnInitListener {

    TextView responseText;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 123;
    APIInterface apiInterface;
    private TextToSpeech textToSpeech;
    private String selectedVoiceName;
    private SpeechRecognizer speechRecognizer;
    private boolean isListening = false;
    private static final int SPEECH_REQUEST_CODE = 101;
    // Define the pic id
    private static final int pic_id = 123;
    // Define the button and imageview type variable
    Button camera_open_id;
    String additionalText;
    ImageView click_image_id;
    private String textToType;
    private int currentIndex;
    private static final long DELAY_MILLIS = 100;
    TextView labeltext;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private boolean instructionsSpoken = false;
    int intValues, intVal, s=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        selectedVoiceName = intent.getStringExtra("selectedVoiceName");
        intValues= intent.getIntExtra("intValue",0);
        intVal = intent.getIntExtra("intVal",0);
        textToSpeech = new TextToSpeech(this, this);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        // By ID we can get each component which id is assigned in XML file get Buttons and imageview.
       // camera_open_id = findViewById(R.id.camera_button);
        click_image_id = findViewById(R.id.click_image);
        responseText = findViewById(R.id.responseText);
        responseText.setVisibility(View.VISIBLE);
        responseText.setText("ok");
        labeltext = findViewById(R.id.labelText);
        if (intVal==0){

        }
        else {FontSizeUtil.setDefaultTextSize(this, getWindow().getDecorView(), intValues);}
        if (Build.VERSION.SDK_INT >= 30){
            if (!Environment.isExternalStorageManager()){
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }


            // Create the camera_intent ACTION_IMAGE_CAPTURE, it will open the camera for capturing the image
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Start the activity with camera_intent and request pic id
            startActivityForResult(camera_intent, pic_id);

            // Schedule a task to capture an image after 5 seconds// 5000 milliseconds (5 seconds)

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (!instructionsSpoken) {
//          //  speakInstructionsAndStartListening();
//            instructionsSpoken = true; // Set the flag to true after speaking the instructions
//        }
//    }

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
       // textToSpeech.speak("Let's us help you select the voice preference. You can select between a male voice and a female voice. Click on Male or Female option or just speak 'Male' to select the male voice, OR speak 'Female' to select a female voice so that you can hear sample voices and select the one according to your choice", TextToSpeech.QUEUE_FLUSH, null);
        //textToSpeech.speak("You're now on the Camera Screen. We need to open your camera app in order to describe the scene around you. Should we open the camera app for you? If yes, please say 'YES'. If no, Please say 'No'.", TextToSpeech.QUEUE_FLUSH, null);
        textToSpeech.speak("You're now on the Camera Screen. Your camera is open now. Please say click to click the picture.", TextToSpeech.QUEUE_FLUSH, null);

       // PostDelayed(20000);

    }
    public void PostDelayed(int a) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openVoiceRecognitionForPreference();
            }
        }, a);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopListening(); // Stop it here

        if (speechRecognizer != null) {
            speechRecognizer.cancel();
            speechRecognizer.destroy();
        }
        new Handler().removeCallbacksAndMessages(null);
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

    // This method will help to retrieve the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Match the request 'pic id with requestCode

            if (s==0 && requestCode == pic_id && resultCode == RESULT_OK) {
                // BitMap is a data structure of the image file which stores the image in memory
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                // Set the image in the ImageView for display
                click_image_id.setImageBitmap(photo);

                // Send the captured image to the Flask API
                sendImageToFlask(photo);

                // Start voice recognition after capturing the image
               // startListening();
                Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show();

                s=1;



            } else if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == Activity.RESULT_OK ) {
                // Handle voice recognition result
                ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0).toLowerCase();
                    if (recognizedText.equals("yes")) {
                        // User wants to capture more images
                        Toast.makeText(this, "yes", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity2.class);
                        intent.putExtra("selectedVoiceName", selectedVoiceName);
                        intent.putExtra("intValue", intValues);
                        intent.putExtra("intVal", 5);
                        startActivity(intent);
                        finish();
                    } else if (recognizedText.equals("no")) {
                        // User does not want to capture more images
                        // Handle accordingly
                    }
                }
            }
        }

    // Method to send the captured image to the Flask API
    private void sendImageToFlask(Bitmap photo) {
        // Convert the Bitmap to a byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // Create a RequestBody for the image data
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), byteArray);

        // Create a MultipartBody.Part with the image data
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "image.png", requestFile);

        // Make the API call to process the image
        Call<ImageResponse> call = apiInterface.processImage(body);
        System.out.println("brr");
        textToSpeech.speak("Please wait", TextToSpeech.QUEUE_FLUSH, null);
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {

                if (response.isSuccessful()) {

                    // Handle the response, which will contain the annotated image
                    // You can extract the image data from the response body

                    //ResponseBody responseBody = response.body();
                    ImageResponse imageResponse = response.body();
                    System.out.println("brr"+  imageResponse.getAdditionalText());
                    //String labelText = imageResponse.getLabels();
                    //String additionalText = imageResponse.getAdditionalText();
                    List<String> labelList = imageResponse.getLabels();
                    List<String> additionalTextList = imageResponse.getAdditionalText();

// Extract the first element from each list (if they are not empty)
                    String labelText = labelList.isEmpty() ? null : labelList.get(0);
                    additionalText = additionalTextList.isEmpty() ? null : additionalTextList.get(0);
                    //ByteArrayInputStream inputStream = new ByteArrayInputStream(imageResponse.getImage());
                    String base64Image = imageResponse.getImage();
                    byte[] imageData =  Base64.decode(base64Image, Base64.DEFAULT);

                    //InputStream inputStream = imageResponse.byteStream();

                    System.out.println("brr"+  labelText);

                    // Process the InputStream or display the image as needed
                    // For example, you can convert the InputStream to a Bitmap

                    //Bitmap annotatedImage = BitmapFactory.decodeStream(inputStream);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

                    System.out.println("brr"+  bitmap);

                    // Update your UI with the annotated image
                    click_image_id.setImageBitmap(bitmap);
                    responseText.setText(additionalText);
                  //  Toast.makeText(MainActivity2.this, ""+responseText, Toast.LENGTH_SHORT).show();
                    showTextWithAnimation();
                    labeltext.setText("Objects detected: "+ labelList.toString());
                    textToSpeech.speak("There is "+additionalText, TextToSpeech.QUEUE_FLUSH, null);
                    Log.d("MyApp", "additionalText: " + additionalText);
                    Log.d("MyApp", "responseText: " + responseText.getText());

                   // textToSpeech.speak("Do you want to capture more? If yes, please say YES else please say NO", TextToSpeech.QUEUE_FLUSH, null);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textToSpeech.speak("Do you want to capture more.If yes, please say YES else please say NO", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }, 10000);
                    PostDelayed(15000);

//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                           // textToSpeech.speak("You're now on the Camera Screen. Your camera is open now. Please say click to capture the picture.", TextToSpeech.QUEUE_FLUSH, null);
//                            textToSpeech.speak("Do you want to capture more ? If yes, please say 'Yes' else please say 'No' ", TextToSpeech.QUEUE_FLUSH, null);
//                        }
//                    }, 4000);


                    Toast.makeText(MainActivity2.this, "Objects: " + labelText, Toast.LENGTH_SHORT).show();


                } else {
                    // Handle the error
                    Toast.makeText(MainActivity2.this, "Error1: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            private void showTextWithAnimation() {
                if (additionalText != null && !additionalText.isEmpty()) {
                    responseText.setText(""); // Clear the responseText TextView

                    final int[] currentIndex = {0}; // Using an array to make it effectively final

                    // Use a handler to post delayed messages to update the TextView gradually
                    final Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            // Update the responseText TextView with one letter at a time
                            responseText.append(String.valueOf(additionalText.charAt(currentIndex[0])));
                            currentIndex[0]++;
                            int redColor = Color.parseColor("#FF0000");
                            int whiteColor = Color.parseColor("#FFFFFF");
                            responseText.setBackgroundColor(redColor);
                            responseText.setTextColor(whiteColor);

                            // Schedule the next update if there are more letters
                            if (currentIndex[0] < additionalText.length()) {
                                handler.postDelayed(this, DELAY_MILLIS);
                            }
                        }
                    };

                    handler.postDelayed(runnable, DELAY_MILLIS);
                }
            }


            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                // Handle the failure
                String errorMessage;
                if (t instanceof IOException) {
                    errorMessage = "Network error: " + t.getMessage();
                } else {
                    errorMessage = "Unexpected error: " + t.getMessage();
                }
                Toast.makeText(MainActivity2.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
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
            textToSpeech.speak("You're now on the Camera Screen. Your camera is open now. If you want us to describe the scene present around you. Please click the volume down button to capture an image.", TextToSpeech.QUEUE_FLUSH, null);

        }
    }

}
