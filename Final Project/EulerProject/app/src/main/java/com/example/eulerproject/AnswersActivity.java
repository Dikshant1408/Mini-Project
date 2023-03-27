package com.example.eulerproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eulerproject.Adapters.AnswersAdapter;

public class AnswersActivity extends AppCompatActivity {

    private RecyclerView answersView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);

        answersView = findViewById(R.id.aa_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        answersView.setLayoutManager(layoutManager);

        AnswersAdapter adapter = new AnswersAdapter(DbQuery.g_questList);
        answersView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            AnswersActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}