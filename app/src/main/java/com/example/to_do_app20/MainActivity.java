package com.example.to_do_app20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.SplittableRandom;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private DatabaseHandler db;
    TextInputEditText username, password;
    Button btnLogin,btnRegister;
    CheckBox checkBox;
    static boolean keepMeLoggedIn = false;
    Context context = this;
    static String tokenX;
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.edUsername);
        password = findViewById(R.id.edPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        checkBox = findViewById(R.id.checkBox);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(username.getText().toString()) && TextUtils.isEmpty(password.getText().toString())){
                    Toast.makeText(MainActivity.this,"Username and Password Required", Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(username.getText().toString()) ){
                    Toast.makeText(MainActivity.this,"Username Required", Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(password.getText().toString())){
                    Toast.makeText(MainActivity.this,"Password Required", Toast.LENGTH_LONG).show();
                }else{
                    keepMeLoggedIn = checkBox.isChecked();


                    login();
                }

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterScreen.class));
            }
        });
    }

    public void login(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username.getText().toString());
        loginRequest.setPassword(password.getText().toString());

        Call<LoginResponse> loginResponseCall = ApiClient.getUserService().userLogin(loginRequest);
        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Login Successful", Toast.LENGTH_SHORT).show();
                    LoginResponse loginResponse = response.body();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(keepMeLoggedIn) {
                                db = new DatabaseHandler(context);
                                db.openDatabase();
                                TokenModel tokenModel = new TokenModel();
                                tokenModel.setToken(loginResponse.getToken());
                                db.insertToken(tokenModel);
                            }else{
                                keepMeLoggedIn = true;
                                tokenX = loginResponse.getToken();
                            }
                            (MainActivity.this).finishAffinity();
                            startActivity(new Intent(MainActivity.this, TaskList.class));


                        }
                    }, 0);

                }else{
                    Toast.makeText(MainActivity.this,"Login Failed", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Throwable "+t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }


        });





    }

}