package com.example.se;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.se.APIInterface;
//import com.example.se.pojo.MultipleResource.ImageResponse;
import com.example.se.pojo.ImageQuery;
import com.example.se.pojo.ImageResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;

public class MainActivity2 extends AppCompatActivity {

    TextView responseText;
    APIInterface apiInterface;
    // Define the pic id
    private static final int pic_id = 123;
    // Define the button and imageview type variable
    Button camera_open_id;
    ImageView click_image_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        // By ID we can get each component which id is assigned in XML file get Buttons and imageview.
        camera_open_id = findViewById(R.id.camera_button);
        click_image_id = findViewById(R.id.click_image);
        responseText = findViewById(R.id.responseText);
        if (Build.VERSION.SDK_INT >= 30){
            if (!Environment.isExternalStorageManager()){
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }

        // Camera_open button is for opening the camera and adding the setOnClickListener in this button
        camera_open_id.setOnClickListener(v -> {
            // Create the camera_intent ACTION_IMAGE_CAPTURE, it will open the camera for capturing the image
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Start the activity with camera_intent and request pic id
            startActivityForResult(camera_intent, pic_id);
        });

    }

    // This method will help to retrieve the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Match the request 'pic id with requestCode
        if (requestCode == pic_id && resultCode == RESULT_OK) {
            // BitMap is a data structure of the image file which stores the image in memory
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            // Set the image in the ImageView for display
            click_image_id.setImageBitmap(photo);

            // Send the captured image to the Flask API
            sendImageToFlask(photo);
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
                    String additionalText = additionalTextList.isEmpty() ? null : additionalTextList.get(0);
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
                    Toast.makeText(MainActivity2.this, "Objects: " + labelText, Toast.LENGTH_SHORT).show();

                } else {
                    // Handle the error
                    Toast.makeText(MainActivity2.this, "Error1: " + response.message(), Toast.LENGTH_SHORT).show();
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

    private void sendQueryToFlask(String query) {

        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), query);

        MultipartBody.Part body = MultipartBody.Part.createFormData("query", query);

        Call<ImageQuery> call = apiInterface.processQuery(body);
        System.out.println("brr");
        call.enqueue(new Callback<ImageQuery>() {
            @Override
            public void onResponse(Call<ImageQuery> call, Response<ImageQuery> response) {

                if (response.isSuccessful()) {

                    ImageQuery imageQuery = response.body();
                    System.out.println("brr"+  imageQuery.getAdditionalText());
                    List<String> additionalTextList = imageQuery.getAdditionalText();

                    String additionalText = additionalTextList.isEmpty() ? null : additionalTextList.get(0);


                    responseText.setText(additionalText);
                    Toast.makeText(MainActivity2.this, "caption: " + additionalText, Toast.LENGTH_SHORT).show();

                } else {
                    // Handle the error
                    Toast.makeText(MainActivity2.this, "Error1: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ImageQuery> call, Throwable t) {
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
}
