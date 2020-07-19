package com.dunesart.android.bitquiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dunesart.android.bitquiz.Model.QuestionScore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Done extends AppCompatActivity {
    Button btnTryAgain;
    TextView txtResultScore, getTxtResultQuestion;
    ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference quesiton_score;
    DatabaseReference users;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);
        database = FirebaseDatabase.getInstance();
        quesiton_score = database.getReference("Question_Score");
        users = database.getReference("Users");

        txtResultScore = findViewById(R.id.txtTotalScore);
        getTxtResultQuestion = findViewById(R.id.txtTotalQuestion);
        progressBar = findViewById(R.id.doneProgressBar);

        btnTryAgain = findViewById(R.id.btnTryAgain);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Done.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            int score = extra.getInt("SCORE");
            int totalQuestion = extra.getInt("TOTAL");
            int correctAnswer = extra.getInt("CORRECT");


            int newTotalScore = Common.currentUser.getScore() + score;
            Common.currentUser.setScore(newTotalScore);

            //update user in firebase
            users.child(Common.currentUser.getUserName()).child("score").setValue(newTotalScore);

            txtResultScore.setText(String.format("SCORE : %d", score));
            getTxtResultQuestion.setText(String.format("PASSED : %d/%d", correctAnswer, totalQuestion));

            progressBar.setMax(totalQuestion);
            progressBar.setProgress(correctAnswer);
            progressBar.getProgressDrawable().setColorFilter(
                    Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);

            quesiton_score.child(String.format("%s_%s", Common.currentUser.getUserName(),
                    Common.CategoryId))
                    .setValue(new QuestionScore(
                            String.format("%s_%s", Common.currentUser.getUserName(),
                                    Common.CategoryId), Common.currentUser.getUserName(), String.valueOf(score)
                            , Common.CategoryId, Common.CategoryName));

        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isConnected = checkNetwork();

        if (!isConnected){
            //create a dialog to tell the user to on his data
            CreateNetworkStatusDialog();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean isConnected = checkNetwork();

        if (!isConnected){
            //create a dialog to tell the user to on his data
            CreateNetworkStatusDialog();
        }

    }

    public boolean checkNetwork(){

        boolean isConnected;

        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;

    }

    public void CreateNetworkStatusDialog(){

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(Done.this);
        alertdialog.setTitle("Network Status");
        alertdialog.setMessage("We were unable to detect a network connection. A network connection is required to use this Application");
        alertdialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Done.this, Done.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });

        alertdialog.show();

    }

}
