package com.example.eulerproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eulerproject.Fragement.LeaderboardFragment;

import java.util.concurrent.TimeUnit;

public class ScoreActivity extends AppCompatActivity {

    private TextView scoreTV, timeTV, totalQTV, correctQTV, wrongQTV, unattemptedQTV;
    Button leaderB, reAttemptB, viewAnsB;
    private long timeTaken;
    private Toolbar toolbar;
    private Dialog progressDialog;
    private TextView dialogText;
    private int finalScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        getSupportActionBar().setTitle("RESULT:");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new Dialog(ScoreActivity .this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText = progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Loading ...");

        init();

        loadData();

        viewAnsB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScoreActivity.this, AnswersActivity.class);
                startActivity(intent);
            }
        });

        reAttemptB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reAttempt();
            }
        });

        leaderB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .add(android.R.id.content, new LeaderboardFragment ()).commit();
            }
        });

        saveResult();

    }

    private void init(){

        scoreTV = findViewById(R.id.score);
        timeTV = findViewById(R.id.time);
        totalQTV = findViewById(R.id.totalQ);
        correctQTV = findViewById(R.id.correctQ);
        wrongQTV = findViewById(R.id.wrongQ);
        unattemptedQTV = findViewById(R.id.un_attemptedQ);
        leaderB = findViewById(R.id.leaderboardB);
        reAttemptB = findViewById(R.id.reattemptB);
        viewAnsB = findViewById(R.id.view_answerB);

    }

    private void loadData(){


        int correctQ = 0, wrongQ = 0, unattempyQ = 0;

        try {
            for (int i=0; i < DbQuery.g_questList.size(); i++){
                if (DbQuery.g_questList.get(i).getSelectedAns() == -1){
                    unattempyQ ++;
                    Log.println(Log.INFO, "unattem", unattempyQ + "");
                } else {
                    if (DbQuery.g_questList.get(i).getSelectedAns() == DbQuery.g_questList.get(i).getCorrectAns()){
                        correctQ ++;
                        Log.println(Log.INFO, "correc", correctQ + "");
                    } else {
                        wrongQ ++;
                        Log.println(Log.INFO, "wrong", wrongQ + "");
                    }
                }
            }


        } catch (Exception e) {
            // Handle the exception here
            e.printStackTrace();
        }

        correctQTV.setText(String.valueOf(correctQ));
        wrongQTV.setText(String.valueOf(wrongQ));
        unattemptedQTV.setText(String.valueOf(unattempyQ));

        totalQTV.setText(String.valueOf(DbQuery.g_questList.size()));

        finalScore = (correctQ * 100) / DbQuery.g_questList.size();
        finalScore = correctQ;
        scoreTV.setText(String.valueOf(finalScore));

        timeTaken = getIntent().getLongExtra("TIME TAKEN",0);

        @SuppressLint("DefaultLocale") String time = String.format("%02d:%2d min",
                TimeUnit.MILLISECONDS.toMinutes(timeTaken),
                TimeUnit.MILLISECONDS.toSeconds(timeTaken) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeTaken))
        );
        timeTV.setText(time);

    }

    private void reAttempt(){
        for (int i=0; i < DbQuery.g_questList.size(); i++){
            DbQuery.g_questList.get(i).setSelectedAns(-1);
            DbQuery.g_questList.get(i).setStatus(DbQuery.NOT_VISITED);
        }

        Intent intent = new Intent(ScoreActivity.this, StartTestActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveResult(){
        DbQuery.saveResult(finalScore, new MyCompleteListener() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                Toast.makeText(ScoreActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            ScoreActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}