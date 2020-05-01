package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private EditText name, email, age;
    private Button btn;
    private Button btn_upload_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        age = findViewById(R.id.age);

        btn = findViewById(R.id.btn);
        btn_upload_activity = findViewById(R.id.btn_upload_activity);
        btn_upload_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ImageUploadActivity.class );
                startActivity(intent);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(
                        name.getText().toString(),
                        email.getText().toString(),
                        age.getText().toString(),
                        null
                );
                
                sendNetworkRequest(user);
            }
        });

//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl("https://api.github.com/")
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//
//        GitHubClient client = retrofit.create(GitHubClient.class);
//        Call<List<GitHubRepo>> call = client.reposForUser("ikbalsazib");
//
//        call.enqueue(new Callback<List<GitHubRepo>>() {
//            @Override
//            public void onResponse(Call<List<GitHubRepo>> call, Response<List<GitHubRepo>> response) {
//                List<GitHubRepo> repos = response.body();
//                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
//                // Toast.makeText(MainActivity.this, repos.size(), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(Call<List<GitHubRepo>> call, Throwable t) {
//                Toast.makeText(MainActivity.this, "Error: ", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void sendNetworkRequest(User user) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.1.108:8201/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        UserClient client = retrofit.create(UserClient.class);
        Call<User> call =  client.CreateAccount(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    response.body();
                    String message = response.body().getMessageRes();
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                }
                // Toast.makeText(MainActivity.this, "Success " + response.body().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error ):", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
