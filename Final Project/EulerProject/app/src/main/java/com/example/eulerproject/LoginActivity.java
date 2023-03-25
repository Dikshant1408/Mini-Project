package com.example.eulerproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private EditText email,pass;
    private Button loginB;
    private TextView forgotPassB, signUp;
    private FirebaseAuth mAuth;
    private Dialog progressDialog;
    private TextView dialogText;
    private RelativeLayout gsignBtn;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 104;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        loginB = findViewById(R.id.loginB);
       forgotPassB = findViewById(R.id.forgot_pass);
       signUp = findViewById(R.id.signUp);
       gsignBtn = findViewById(R.id.g_signinBtn);

        progressDialog = new Dialog(LoginActivity.this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText = progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Signing In ...");


        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginB.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (validateData()){
                   login();
               }
           }
       });

       signUp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
               startActivity(intent);
               finish();
           }
       });

       gsignBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               googleSignIn();
           }
       });


    }

    private boolean validateData(){

        if ((email.getText().toString().isEmpty())){
            email.setError("Enter Email ID");
            return false;
        }

        if (pass.getText().toString().isEmpty()){
            pass.setError("Enter Password");
            return false;
        }
        return true;
    }

    private void login(){

        progressDialog.show();
        mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), pass.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"Login Success",Toast.LENGTH_SHORT).show();
                            DbQuery.loadCategories(new MyCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onFailure() {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void googleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requesCode, int resultCode, Intent data){
        super.onActivityResult(requesCode, resultCode, data);

        if (requesCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e){
                //Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken){

        progressDialog.show();

            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this, (task) -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this,"Google Sign In Success",Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(task.getResult().getAdditionalUserInfo().isNewUser()){
                                    DbQuery.createUserData(user.getEmail(), user.getDisplayName(), new MyCompleteListener() {
                                        @Override
                                        public void onSuccess() {

                                            DbQuery.loadCategories(new MyCompleteListener() {
                                                @Override
                                                public void onSuccess() {
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                    startActivity(intent);
                                                    LoginActivity.this.finish();
                                                }

                                                @Override
                                                public void onFailure() {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(LoginActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onFailure() {
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    DbQuery.loadCategories(new MyCompleteListener() {
                                        @Override
                                        public void onSuccess() {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            LoginActivity.this.finish();
                                        }

                                        @Override
                                        public void onFailure() {
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                    });
        }
    }