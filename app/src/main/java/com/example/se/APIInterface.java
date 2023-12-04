package com.example.se;

import com.example.se.pojo.ImageQuery;
import com.example.se.pojo.MultipleResource;
import com.example.se.pojo.ImageResponse;
import com.example.se.pojo.User;
import com.example.se.pojo.UserList;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

interface APIInterface {

    @GET("/api/unknown")
    Call<MultipleResource> doGetListResources();

    @POST("/api/users")
    Call<User> createUser(@Body User user);

    @GET("/api/users?")
    Call<UserList> doGetUserList(@Query("page") String page);

    @FormUrlEncoded
    @POST("/api/users?")
    Call<UserList> doCreateUserWithField(@Field("name") String name, @Field("job") String job);


    @Multipart
    @POST("/process-image")
    Call<ImageResponse> processImage(@Part MultipartBody.Part image);

    @Multipart
    @POST("/process-query")
    Call<ImageQuery> processQuery(@Part MultipartBody.Part query);
}