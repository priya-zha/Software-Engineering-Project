package com.example.se;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;

import android.net.Uri;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraStream extends AppCompatActivity {

    TextView responseText;
    APIInterface apiInterface;
    private static final int pic_id = 123;
    Button camera_open_id;
    ImageView click_image_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        camera_open_id = findViewById(R.id.camera_button);
        click_image_id = findViewById(R.id.click_image);
        responseText = (TextView) findViewById(R.id.responseText);

        camera_open_id.setOnClickListener(v -> {

            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            startActivityForResult(camera_intent, pic_id);
        });

        /*
        Call<MultipleResource> call = apiInterface.doGetListResources();
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {


                Log.d("TAG",response.code()+"");

                String displayResponse = "";

                MultipleResource resource = response.body();
                Integer text = resource.page;
                Integer total = resource.total;
                Integer totalPages = resource.totalPages;
                List<MultipleResource.Datum> datumList = resource.data;

                displayResponse += text + " Page\n" + total + " Total\n" + totalPages + " Total Pages\n";

                for (MultipleResource.Datum datum : datumList) {
                    displayResponse += datum.id + " " + datum.name + " " + datum.pantoneValue + " " + datum.year + "\n";
                }

                Toast.makeText(getApplicationContext(), displayResponse, Toast.LENGTH_SHORT).show();
                responseText.setText(displayResponse);

            }

            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                call.cancel();
            }
        });

        User user = new User("morpheus", "leader");
        Call<User> call1 = apiInterface.createUser(user);
        call1.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user1 = response.body();

                Toast.makeText(getApplicationContext(), user1.name + " " + user1.job + " " + user1.id + " " + user1.createdAt, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                call.cancel();
            }
        });

        Call<UserList> call2 = apiInterface.doGetUserList("2");
        call2.enqueue(new Callback<UserList>() {
            @Override
            public void onResponse(Call<UserList> call, Response<UserList> response) {

                UserList userList = response.body();
                Integer text = userList.page;
                Integer total = userList.total;
                Integer totalPages = userList.totalPages;
                List<UserList.Datum> datumList = userList.data;
                Toast.makeText(getApplicationContext(), text + " page\n" + total + " total\n" + totalPages + " totalPages\n", Toast.LENGTH_SHORT).show();

                for (UserList.Datum datum : datumList) {
                    Toast.makeText(getApplicationContext(), "id : " + datum.id + " name: " + datum.first_name + " " + datum.last_name + " avatar: " + datum.avatar, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<UserList> call, Throwable t) {
                call.cancel();
            }
        });

        Call<UserList> call3 = apiInterface.doCreateUserWithField("morpheus","leader");
        call3.enqueue(new Callback<UserList>() {
            @Override
            public void onResponse(Call<UserList> call, Response<UserList> response) {
                UserList userList = response.body();
                Integer text = userList.page;
                Integer total = userList.total;
                Integer totalPages = userList.totalPages;
                List<UserList.Datum> datumList = userList.data;
                Toast.makeText(getApplicationContext(), text + " page\n" + total + " total\n" + totalPages + " totalPages\n", Toast.LENGTH_SHORT).show();

                for (UserList.Datum datum : datumList) {
                    Toast.makeText(getApplicationContext(), "id : " + datum.id + " name: " + datum.first_name + " " + datum.last_name + " avatar: " + datum.avatar, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<UserList> call, Throwable t) {
                call.cancel();
            }
        });
        */

        File imageFile = new File(Environment.getExternalStorageDirectory(), "/Download/Screenshot.png");

        Log.e("body",""+imageFile);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
        Log.e("body",""+requestFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
        Toast.makeText(getApplicationContext(), "hello"+body, Toast.LENGTH_SHORT).show();
        Log.e("body",""+body);
        // Make the API call to process the image
        Call<ResponseBody> call = apiInterface.processImage(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();

                if (response.isSuccessful()) {

                    ResponseBody responseBody = response.body();
                    InputStream inputStream = responseBody.byteStream();
                    //byte[] imageBytes = response.body().bytes();
                    Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();


                    Bitmap annotatedImage = BitmapFactory.decodeStream(inputStream);
                    //Bitmap annotatedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);


                    click_image_id.setImageBitmap(annotatedImage);
                } else {
                    // Handle the error
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "error"+t, Toast.LENGTH_SHORT).show();
                Log.e("body",""+t);

            }
        });


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pic_id) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            click_image_id.setImageBitmap(photo);
        }
    }
}
