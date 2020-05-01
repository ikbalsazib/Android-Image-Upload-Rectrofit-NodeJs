package com.example.myapplication;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ImageServerClient {
    @Multipart
    @POST("upload")
    Call<ImageUpload> uploadImage(
            @Part("folderPath") RequestBody folderPath,
            @Part MultipartBody.Part imageFile
    );
}
