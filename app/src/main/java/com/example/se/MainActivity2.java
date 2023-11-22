package com.example.se;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.se.APIInterface;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        Call<ResponseBody> call = apiInterface.processImage(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    // Handle the response, which will contain the annotated image
                    // You can extract the image data from the response body

                    ResponseBody responseBody = response.body();
                    InputStream inputStream = responseBody.byteStream();

                    // Process the InputStream or display the image as needed
                    // For example, you can convert the InputStream to a Bitmap

                    Bitmap annotatedImage = BitmapFactory.decodeStream(inputStream);

                    // Update your UI with the annotated image
                    click_image_id.setImageBitmap(annotatedImage);
                } else {
                    // Handle the error
                    Toast.makeText(MainActivity2.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle the failure
                Toast.makeText(MainActivity2.this, "Failed to send the image: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
