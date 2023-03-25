package com.example.eulerproject;

import static com.example.eulerproject.DbQuery.g_catList;
import static com.example.eulerproject.DbQuery.loadquestions;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StartTestActivity extends AppCompatActivity {

    private TextView catName, testNo, TotalQ, bestScore, time;
    private Button StartTestB;
    private ImageView backB;
    private Dialog progressDialog;
    private TextView dialogText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_test);

        init();

        progressDialog = new Dialog(StartTestActivity .this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        dialogText = progressDialog.findViewById(R.id.dialog_text);
        dialogText.setText("Loading ...");
        progressDialog.show();

        DbQuery.loadquestions(new MyCompleteListener() {
            @Override
            public void onSuccess() {

                setData();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(StartTestActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(){

        catName = findViewById(R.id.st_cat_name);
        testNo = findViewById(R.id.st_test_no);
        TotalQ = findViewById(R.id.st_total_question);
        bestScore = findViewById(R.id.st_best_score);
        time = findViewById(R.id.st_time);
        StartTestB = findViewById(R.id.start_testB);
        backB = findViewById(R.id.st_back);

        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartTestActivity.this.finish();
            }
        });

        StartTestB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(StartTestActivity.this,QuestionsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setData(){

        catName.setText(g_catList.get(DbQuery.g_selected_cat_index).getName());
        testNo.setText("TEST No. " + String.valueOf(DbQuery.g_selected_test_index + 1));
        TotalQ.setText(String.valueOf(DbQuery.g_questList.size()));
        bestScore.setText(String.valueOf(DbQuery.g_testList.get(DbQuery.g_selected_test_index).getTopScore()));
        time.setText(String.valueOf(DbQuery.g_testList.get(DbQuery.g_selected_test_index).getTime()));
    }
}