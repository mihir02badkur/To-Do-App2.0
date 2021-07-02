package com.example.to_do_app20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterScreen extends AppCompatActivity {
    TextInputEditText name, email, username, password;
    Button btnRegister;
    DatabaseHandler db;
    Context context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        name = findViewById(R.id.edName);
        email = findViewById(R.id.edEmail);
        username = findViewById(R.id.edUsernameRegister);
        password = findViewById(R.id.edPasswordRegister);
        btnRegister = findViewById(R.id.btnRegister2);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUser(createRequest());
            }
        });

    }

    public UserRequest createRequest(){
        UserRequest userRequest = new UserRequest();
        userRequest.setName(name.getText().toString());
        userRequest.setEmail(email.getText().toString());
        userRequest.setUsername(username.getText().toString());
        userRequest.setPassword(password.getText().toString());
        return userRequest;

    }

    public void saveUser(UserRequest userRequest){
        Call<UserResponse> userResponseCall = ApiClient.getUserService().saveUser(userRequest);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(RegisterScreen.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                    Toast.makeText(RegisterScreen.this, "Create your First Task", Toast.LENGTH_LONG).show();
                    UserResponse userResponse = response.body();
                    db = new DatabaseHandler(context);
                    db.openDatabase();
                    TokenModel tokenModel = new TokenModel();
                    tokenModel.setToken(userResponse.getToken());
                    db.insertToken(tokenModel);
                    MainActivity.keepMeLoggedIn = true;
                    MainActivity.tokenX = userResponse.getToken();
                    startActivity(new Intent(RegisterScreen.this, TaskList.class));
                }else{
                    Toast.makeText(RegisterScreen.this, "Request failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(RegisterScreen.this, "Request failed"+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}