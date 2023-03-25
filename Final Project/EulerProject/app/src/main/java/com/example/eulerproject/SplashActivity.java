package com.example.eulerproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private TextView appName;
    private FirebaseAuth mAuth;
    private ImageView applogo;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        applogo = findViewById(R.id.applogo);
        appName = findViewById(R.id.app_name);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);

        appName.setTypeface(typeface);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.myanim);
        appName.setAnimation(anim);
        applogo.setAnimation(anim);

        mAuth = FirebaseAuth.getInstance();
        DbQuery.g_firstore = FirebaseFirestore.getInstance();

        new Thread(){
            @Override
            public void run()
            {
                try {
                    sleep(3000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

                if (mAuth.getCurrentUser() != null){
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }

            }
        }
        .start();
    }
}