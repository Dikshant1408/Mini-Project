package com.example.projecteuler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_in);


        EditText eduUsername, eduPassword;
        Button button;

        eduUsername = findViewById(R.id.inputEmail);
        eduPassword = findViewById(R.id.inputPassword);
        button = findViewById(R.id.btnlogin);
        TextView btn = findViewById(R.id.tvsignUp);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = eduUsername.getText().toString();
                String password = eduPassword.getText().toString();
                Database db = new Database(getApplicationContext(), "EulerProject", null, 1);

                if (username.length() == 0 || password.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Please fill all the Details", Toast.LENGTH_SHORT).show();
                } else {
                    if (db.logic(username, password) == 1) {
                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                        SharedPreferences sharedPreferences = getSharedPreferences("share_pref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username);
                        editor.apply();
                        startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
    }
}