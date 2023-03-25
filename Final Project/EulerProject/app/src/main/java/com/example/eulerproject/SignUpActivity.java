package com.example.eulerproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.ktx.Firebase;

import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity {

    private EditText name, email, pass, confirmPass;
    private Button SignUp;
    private TextView backB;
    private FirebaseAuth mAuth;
    private String emailstr, passStr, confirmPassStr, usernameStr;
    private Dialog progressDialog;
    private TextView dialogText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.username);
        email = findViewById(R.id.emailID);
        pass = findViewById(R.id.password);
        confirmPass = findViewById(R.id.confirm_pass);
        backB = findViewById(R.id.backB);
        SignUp = findViewById(R.id.signupB);

        progressDialog = new Dialog(SignUpActivity.this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText = progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Registering User...");

        mAuth = FirebaseAuth.getInstance();

        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()){
                    signUpNewUser();
                }
            }
        });
    }

    private boolean validate(){
        usernameStr = name.getText().toString().trim();
        passStr = pass.getText().toString().trim();
        emailstr = email.getText().toString().trim();
        confirmPassStr = confirmPass.getText().toString().trim();

        if (usernameStr.isEmpty()){
            name.setError("Enter your name");
            return false;
        }

        if (emailstr.isEmpty()){
            email.setError("Enter your email");
            return false;
        }

        if (passStr.isEmpty()){
            pass.setError("Enter your password");
            return false;
        }

        if (confirmPassStr.isEmpty()){
            confirmPass.setError("Confirm your password");
            return false;
        }

        if (passStr.compareTo(confirmPassStr) != 0){
            Toast.makeText(SignUpActivity.this, "Password and confirm password are not same", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void signUpNewUser(){

        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(emailstr, passStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this,"Sign Up Successful", Toast.LENGTH_SHORT).show();
                            DbQuery.createUserData(emailstr, usernameStr, new MyCompleteListener(){

                                @Override
                                public void onSuccess() {

                                    DbQuery.loadCategories(new MyCompleteListener() {
                                        @Override
                                        public void onSuccess() {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onFailure() {
                                            Toast.makeText(SignUpActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    });
                                }

                                @Override
                                public void onFailure() {
                                    Toast.makeText(SignUpActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });

                        }else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }
}