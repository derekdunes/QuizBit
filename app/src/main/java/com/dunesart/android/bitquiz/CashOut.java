package com.dunesart.android.bitquiz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CashOut extends AppCompatActivity {


    EditText editText;
    TextView pointText, bitCoinText;
    Button cashOutButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashout);

        editText = findViewById(R.id.cash_out_mail);
        pointText = findViewById(R.id.bits);
        bitCoinText = findViewById(R.id.sats);
        cashOutButton = findViewById(R.id.btn_cash_out);

        editText.setText(Common.currentUser.getEmail());

        pointText.setText(Integer.toString(Common.currentUser.getScore()));

        double bitcoinScore = Common.currentUser.getScore() * 0.000000001;

        Toast.makeText(this, "The bitcoinScore is " + bitcoinScore, Toast.LENGTH_SHORT).show();

        bitCoinText.setText(Double.toString(bitcoinScore));

        cashOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send the request to coinbase

                //if the request is positive
                //set user score to zero


            }
        });

    }
}
