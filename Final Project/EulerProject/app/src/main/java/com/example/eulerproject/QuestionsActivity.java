package com.example.eulerproject;

import static com.example.eulerproject.DbQuery.ANSWERED;
import static com.example.eulerproject.DbQuery.NOT_VISITED;
import static com.example.eulerproject.DbQuery.REVIEW;
import static com.example.eulerproject.DbQuery.UNANSWERED;
import static com.example.eulerproject.DbQuery.g_catList;
import static com.example.eulerproject.DbQuery.g_questList;
import static com.example.eulerproject.DbQuery.g_selected_cat_index;
import static com.example.eulerproject.DbQuery.g_selected_test_index;
import static com.example.eulerproject.DbQuery.g_testList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eulerproject.Adapters.QuestionGridAdapter;
import com.example.eulerproject.Adapters.QuestionsAdapter;

import java.util.concurrent.TimeUnit;

public class QuestionsActivity extends AppCompatActivity {

    private RecyclerView questionView;
    private TextView tvQuesID, timerTV, catNAmeTV;
    private Button submitB, markB, clearSelB;
    private ImageButton prevQuesB, nextQuesB , drawerCloseB;
    private ImageView quesListB;
    private int questID;
    QuestionsAdapter quesAdapter;
    private DrawerLayout drawer;
    private GridView quesListGV;
    private ImageView markImage;
    private QuestionGridAdapter gridAdapter;
    private CountDownTimer timer;
    private long timerLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_lis_layout);

        init();

        quesAdapter = new QuestionsAdapter(DbQuery.g_questList);
        questionView.setAdapter(quesAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        questionView.setLayoutManager(layoutManager);

        gridAdapter = new QuestionGridAdapter(this,g_questList.size());
        quesListGV.setAdapter(gridAdapter);

        setSnapHelper();

        setClickListner();

        startTimer();
    }

    @SuppressLint("SetTextI18n")
    private void init(){

        questionView = findViewById(R.id.question_view);
        tvQuesID = findViewById(R.id.tv_quesID);
        timerTV = findViewById(R.id.tv_timer);
        catNAmeTV = findViewById(R.id.qa_catName);
        submitB = findViewById(R.id.submitB);
        markB =findViewById(R.id.markB);
        clearSelB = findViewById(R.id.clear_selB);
        prevQuesB = findViewById(R.id.prev_quesB);
        nextQuesB = findViewById(R.id.next_quesB);
        quesListB = findViewById(R.id.ques_list_gridB);
        drawer = findViewById(R.id.drawer_layout);
        drawerCloseB = findViewById(R.id.drawer_closeB);
        markImage = findViewById(R.id.mark_image);
        quesListGV = findViewById(R.id.ques_list_gv);

        questID = 0;

        tvQuesID.setText("1/" + String.valueOf(g_questList.size()));
        catNAmeTV.setText(g_catList.get(g_selected_cat_index).getName());

        g_questList.get(0).setStatus(UNANSWERED);
    }

    private void setSnapHelper(){

        final SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(questionView);

        questionView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View view = snapHelper.findSnapView(recyclerView.getLayoutManager());
                questID = recyclerView.getLayoutManager().getPosition(view);

                if (g_questList.get(questID).getStatus() == NOT_VISITED)
                    g_questList.get(questID).setStatus(UNANSWERED);

                if (g_questList.get(questID).getStatus() == REVIEW){
                    markImage.setVisibility(View.VISIBLE);
                } else {
                    markImage.setVisibility(View.GONE);
                }

                tvQuesID.setText(String.valueOf(questID + 1) + "/" + String.valueOf(g_questList.size()));
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setClickListner(){
        prevQuesB.setOnClickListener(view ->{
            if (questID > 0){
                questionView.smoothScrollToPosition(questID - 1);
            }
        });

        nextQuesB.setOnClickListener(view -> {
            if (questID < g_questList.size() - 1){
                questionView.smoothScrollToPosition(questID + 1);
            }
        });

        clearSelB.setOnClickListener(view -> {
            g_questList.get(questID).setSelectedAns(-1);
            g_questList.get(questID).setStatus(UNANSWERED);
            markImage.setVisibility(View.GONE);
            quesAdapter.notifyDataSetChanged();
        });

        quesListB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! drawer.isDrawerOpen(GravityCompat.END)){

                    gridAdapter.notifyDataSetChanged();
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });

        drawerCloseB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerOpen(GravityCompat.END)){
                    drawer.closeDrawer(GravityCompat.END);
                }

            }
        });

        markB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (markImage.getVisibility() != View.VISIBLE){
                    markImage.setVisibility(View.VISIBLE);
                    g_questList.get(questID).setStatus(REVIEW);
                } else {
                    markImage.setVisibility(View.GONE);

                    if (g_questList.get(questID).getSelectedAns() != -1){
                        g_questList.get(questID).setStatus(ANSWERED);
                    } else {
                        g_questList.get(questID).setStatus(UNANSWERED);
                    }
                }
            }
        });

        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submitTest();
            }
        });
    }

    private void submitTest(){
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsActivity.this);
        builder.setCancelable(true);

        View view = getLayoutInflater().inflate(R.layout.alert_dialog_layout,null);

        Button cancelB = view.findViewById(R.id.cancelB);
        Button confirmB = view.findViewById(R.id.confirmB);

        builder.setView(view);

        AlertDialog alertDialog = builder.create();

        cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        confirmB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timer.cancel();
                alertDialog.dismiss();

                Intent intent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                long totaltime = g_testList.get(g_selected_test_index).getTime()*60*1000;
                intent.putExtra("TIME TAKEN",totaltime - timerLeft);
                startActivity(intent);
                QuestionsActivity.this.finish();
            }
        });

        alertDialog.show();
    }

    public void goToQuestions(int position){
        questionView.smoothScrollToPosition(position);

        if (drawer.isDrawerOpen(GravityCompat.END))
            drawer.closeDrawer(GravityCompat.END);
    }

    private void startTimer(){
        long totalTime = g_testList.get(g_selected_test_index).getTime()*60*1000;

        timer = new CountDownTimer(totalTime + 1000, 1000) {
            @Override
            public void onTick(long remainingTime) {

                timerLeft = remainingTime;
                @SuppressLint("DefaultLocale") String time = String.format("%02d:%2d min",
                        TimeUnit.MILLISECONDS.toMinutes(remainingTime),
                        TimeUnit.MILLISECONDS.toSeconds(remainingTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime))
                        );

                timerTV.setText(time);
            }

            @Override
            public void onFinish() {

                Intent intent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                long totaltime = g_testList.get(g_selected_test_index).getTime()*60*1000;
                intent.putExtra("TIME TAKEN",totaltime - timerLeft);
                startActivity(intent);
                QuestionsActivity.this.finish();
            }
        };

        timer.start();
    }
}