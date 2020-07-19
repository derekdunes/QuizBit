package com.dunesart.android.bitquiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dunesart.android.bitquiz.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    //Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    public static final String LOG = SplashScreen.class.getName();

    DatabaseReference users;
    FirebaseAuth auth;
    String Tag = SplashScreen.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        boolean isConnected = checkNetwork();

        if (!isConnected){
            //create a dialog to tell the user to on his data
            CreateNetworkStatusDialog();
        }

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        users = FirebaseDatabase.getInstance().getReference("Users");

        if (auth.getCurrentUser() != null) {

            if (auth.getCurrentUser().getEmail() == null){
                CreateNetworkStatusDialog();
            }

            final FirebaseUser userM = auth.getCurrentUser();
            Log.e(Tag, "Auth.getCurrentUser is true");
            final String[] strings = userM.getEmail().split("@");

            Log.e(Tag, "The email is " + userM.getEmail());
            Log.e(Tag, "The username is " + strings[0]);

            users.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //split the email to get the username

                    if (dataSnapshot.child(strings[0]).exists()) {
                        Common.currentUser = dataSnapshot.child(strings[0]).getValue(User.class);

                        Log.e(Tag, "The User in SplshScreenActivity is " + Common.currentUser);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this,LoginActivity.class);
                startActivity(i);
                finish();
            }

            },SPLASH_TIME_OUT);



    }

    @Override
    protected void onResume() {
        //LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,new IntentFilter("registration complete"));
        //LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,new IntentFilter(Common.STR_PUSH));
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

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(SplashScreen.this);
        alertdialog.setTitle("Network Status");
        alertdialog.setMessage("We were unable to detect a network connection. A network connection is required to use this Application");
        alertdialog.setNeutralButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(SplashScreen.this, SplashScreen.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });

        alertdialog.show();

    }
}
