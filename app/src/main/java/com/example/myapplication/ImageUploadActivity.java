package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;



import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;

public class ImageUploadActivity extends AppCompatActivity {

    Uri picUri;
    private static final int STORAGE_REQUEST_CODE = 101;
    private static final int REQUESTCODE = 2 ;
    private Uri pickedImgUri;
    ImageView imageView;

    Bitmap mBitmap;
    TextView textView;
    // ....
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private final static int IMAGE_RESULT = 200;
    Button uploadBtn;
    Button fabCamera, fabUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        imageView = findViewById(R.id.imageView);

        fabCamera = findViewById(R.id.upload_btn_pick);
        fabUpload = findViewById(R.id.upload_btn);

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        askPermissions();
        // initRetrofitClient();
    }


    // Permission

    private void askPermissions() {
        permissions.add(CAMERA);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
    }


    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    public void pickImage() {

        if (ContextCompat.checkSelfPermission(ImageUploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ImageUploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(ImageUploadActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(ImageUploadActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
            }

        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent,REQUESTCODE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUESTCODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            pickedImgUri = data.getData();
            imageView.setImageURI(pickedImgUri);
        }
    }

    // Upload Image to Server..
    public void uploadImage() {

        File imageFile;
        try {
            imageFile = FileUtil.from(this, getImageUriFromBitmap(this));
            Toast.makeText(this, "Uploading..." + imageFile.getName(), Toast.LENGTH_SHORT).show();
            Log.d("file", "File...:::: uti - "+imageFile .getPath()+" file -" + imageFile + " : " + imageFile .exists());


            RequestBody folderPathPart = RequestBody.create(okhttp3.MultipartBody.FORM, "android_pie");

            RequestBody imageFilePart = RequestBody.create(
                    MediaType.parse("image/jpeg"),
                    imageFile
            );

            MultipartBody.Part file = MultipartBody.Part.createFormData("imageFile", imageFile.getName(), imageFilePart);


            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.108:8201/")
                    .addConverterFactory(GsonConverterFactory.create());

            Retrofit retrofit = builder.build();

            ImageServerClient client = retrofit.create(ImageServerClient.class);

            Call<ImageUpload> call = client.uploadImage(folderPathPart, file);

            call.enqueue(new Callback<ImageUpload>() {
                @Override
                public void onResponse(Call<ImageUpload> call, Response<ImageUpload> response) {
                    if (response.isSuccessful()) {
                        response.body();
                        String message = response.body().getDownloadUrlRes();
                        Toast.makeText(ImageUploadActivity.this, "Success :)" + message, Toast.LENGTH_SHORT).show();
                        // Toast.makeText(ImageUploadActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ImageUpload> call, Throwable t) {
                    Toast.makeText(ImageUploadActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    public Uri getImageUriFromBitmap(Context context){
        Uri data = null;
        Bitmap bitmap = ImageUtils.getInstant().getCompressedBitmap(getRealPathFromURI(ImageUploadActivity.this,pickedImgUri),50);
        // Bitmap bitmap = BitmapFactory.decodeFile (pickedImgUri.getPath());
        // bitmap.compress (Bitmap.CompressFormat.JPEG, compressionRatio, new FileOutputStream(pickedImgUri));
        // Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pickedImgUri);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path.toString());
    }

    public String getRealPathFromURI(Activity context, Uri contentURI) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }




}
