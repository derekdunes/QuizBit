package com.dunesart.android.bitquiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dunesart.android.bitquiz.Model.QuestionScore;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Collections;

public class Playing extends AppCompatActivity implements View.OnClickListener {
    final static long INTERVAL = 1000;
    final static long TIMEOUT = 10000;

    int progressValue = 0;
    CountDownTimer mCountDown;
    int index = 0, score = 0, thisQuestion = 0, totalQuestion, correctAnwer;

    //widgets
    RelativeLayout relativeLayoutQ;
    RelativeLayout relativeLayoutR;

    ProgressBar progressBar;
    ImageView question_image;
    Button btnA, btnB, btnC, btnD;
    TextView txtScore, txtQuestionNum, question_text;

    Button btnTryAgain;
    TextView txtResultScore, getTxtResultQuestion;
    ProgressBar progressBar2;

    //ads objects
    private RewardedVideoAd mRewardedVideoAd;

    //firebase objects
    FirebaseDatabase database;
    DatabaseReference quesiton_score;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        database = FirebaseDatabase.getInstance();
        quesiton_score = database.getReference("Question_Score");
        users = database.getReference("Users");

        relativeLayoutQ = findViewById(R.id.rLayoutQuestion);
        relativeLayoutR = findViewById(R.id.rLayoutResult);

        relativeLayoutQ.setVisibility(View.VISIBLE);
        relativeLayoutR.setVisibility(View.INVISIBLE);

        //playing view widgets
        txtScore = findViewById(R.id.txtScore);
        txtQuestionNum = findViewById(R.id.txtTotalQuestion);
        question_text = findViewById(R.id.question_text);
        question_image = findViewById(R.id.question_image);

        //Done view widgets (new)
        txtResultScore = findViewById(R.id.txtTotalScore);
        getTxtResultQuestion = findViewById(R.id.txtTotalQuestion1);
        progressBar2 = findViewById(R.id.doneProgressBar);

        btnTryAgain = findViewById(R.id.btnTryAgain);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Playing.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        //continuation
        Collections.shuffle(Common.list_question);
        progressBar = findViewById(R.id.progressBar);
        progressBar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        btnA = findViewById(R.id.btnAnswerA);
        btnB = findViewById(R.id.btnAnswerB);
        btnC = findViewById(R.id.btnAnswerC);
        btnD = findViewById(R.id.btnAnswerD);
        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);

        //initialize the Google Mobile Ads
        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_id));

        //Get reference to singleton
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);

        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                Toast.makeText(getBaseContext(),
                        "Ad loaded.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdOpened() {
                Toast.makeText(getBaseContext(),
                        "Ad opened.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoStarted() {
                Toast.makeText(getBaseContext(),
                        "Ad started.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdClosed() {
                Toast.makeText(getBaseContext(),
                        "Ad closed.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                Toast.makeText(getBaseContext(),
                        "Ad triggered reward. Reward amount is " + rewardItem.getAmount(), Toast.LENGTH_SHORT).show();
                score = score + rewardItem.getAmount();

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                Toast.makeText(getBaseContext(),
                        "Ad left application.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Toast.makeText(getBaseContext(),
                        "Ad failed to load.", Toast.LENGTH_SHORT).show();
            }
        });

        // Load a reward based video ad
        mRewardedVideoAd.loadAd(getString(R.string.ad_unit_id), new AdRequest.Builder().build());

    }

    @Override
    public void onClick(View v) {

        mCountDown.cancel();
        //still have question in list
        if (index < totalQuestion) {
            Button clickedButton = (Button) v;
            if (clickedButton.getText().equals(Common.list_question.get(index).getCorrectAnswer())) {
                score = score + 1;
                correctAnwer++;
                showQuestion(++index);


            } else {

                showResult(score, correctAnwer, totalQuestion);

            }
            txtScore.setText(String.format("%d", score));

        }


    }

    private void showQuestion(int index) {
        if (index < totalQuestion) {
            thisQuestion++;
            txtQuestionNum.setText(String.format("%d / %d", thisQuestion, totalQuestion));
            progressBar.setProgress(0);
            progressValue = 0;
            if (Common.list_question.get(index).getIsImageQuestion().equals("true")) {
                Picasso.with(getBaseContext())
                        .load(Common.list_question.get(index).getQuestion())
                        .into(question_image);
                question_image.setVisibility(View.VISIBLE);
                question_text.setVisibility(View.INVISIBLE);

            } else {
                question_text.setText("Q. "+Common.list_question.get(index).getQuestion());
                question_image.setVisibility(View.INVISIBLE);
                question_text.setVisibility(View.VISIBLE);
            }

            btnA.setText(Common.list_question.get(index).getAnswerA());
            btnB.setText(Common.list_question.get(index).getAnswerB());
            btnC.setText(Common.list_question.get(index).getAnswerC());
            btnD.setText(Common.list_question.get(index).getAnswerD());
            mCountDown.start();

        } else {

            showResult(score, correctAnwer, totalQuestion);

        }

    }

    @Override
    protected void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();

        boolean isConnected = checkNetwork();

        if (!isConnected){
            //create a dialog to tell the user to on his data
            CreateNetworkStatusDialog();
        }

        totalQuestion = Common.list_question.size();
        mCountDown = new CountDownTimer(TIMEOUT, INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(progressValue);
                progressValue+=12;

            }

            @Override
            public void onFinish() {
                mCountDown.cancel();
                showQuestion(++index);

            }
        };
        showQuestion(index);
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

    @Override
    protected void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    //helper methods
    public void showRewardedVideo(View view) {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
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

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(Playing.this);
        alertdialog.setTitle("Network Status");
        alertdialog.setMessage("We were unable to detect a network connection. A network connection is required to use this Application");
        alertdialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Playing.this, Playing.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });

        alertdialog.show();

    }

    public void showResult(int score, int correctAnswer, int totalQuestion){

        relativeLayoutQ.setVisibility(View.INVISIBLE);
        relativeLayoutR.setVisibility(View.VISIBLE);

        int newTotalScore = Common.currentUser.getScore() + score;
        Common.currentUser.setScore(newTotalScore);

        //update user in firebase
        users.child(Common.currentUser.getUserName()).child("score").setValue(newTotalScore);

        txtResultScore.setText(String.format("SCORE : %d", score));
        getTxtResultQuestion.setText(String.format("PASSED : %d/%d", correctAnswer, totalQuestion));

        progressBar2.setMax(totalQuestion);
        progressBar2.setProgress(correctAnswer);
        progressBar2.getProgressDrawable().setColorFilter(
                Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);

        quesiton_score.child(String.format("%s_%s", Common.currentUser.getUserName(),
                Common.CategoryId))
                .setValue(new QuestionScore(
                        String.format("%s_%s", Common.currentUser.getUserName(),
                                Common.CategoryId), Common.currentUser.getUserName(), String.valueOf(score)
                        , Common.CategoryId, Common.CategoryName));
    }

}
