package com.dunesart.android.bitquiz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dunesart.android.bitquiz.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    DatabaseReference users;
    private FirebaseAuth auth;
    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    String Tag = LoginActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isConnected = checkNetwork();

        if (!isConnected){
            //create a dialog to tell the user to on his data
            CreateNetworkStatusDialog();
        }

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        users = FirebaseDatabase.getInstance().getReference("Users");
        users.keepSynced(true);

        if (auth.getCurrentUser() != null) {

            final FirebaseUser userM = auth.getCurrentUser();
            Log.e(Tag, "Auth.getCurrentUser is true");

            users.addListenerForSingleValueEvent(new ValueEventListener() {


                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //split the email to get the username
                    String[] strings = userM.getEmail().split("@");

                    Log.e(Tag, "The email is " + userM.getEmail());
                    Log.e(Tag, "The username is " + strings[0]);

                    if (dataSnapshot.child(strings[0]).exists()) {
                        Common.currentUser = dataSnapshot.child(strings[0]).getValue(User.class);
                        Log.e(Tag, "The User in loginActivity is " + Common.currentUser);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            startActivity(new Intent(LoginActivity.this, Home.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        btnSignup = findViewById(R.id.btn_signup);
        btnLogin =  findViewById(R.id.btn_login);
        btnReset = findViewById(R.id.btn_reset_password);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {

                                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String[] strings = email.split("@");
                                            if (dataSnapshot.child(strings[0]).exists()) {
                                                User user = dataSnapshot.child(strings[0]).getValue(User.class);
                                                if (user.getPassword().equals(password)) {
                                                    Common.currentUser = user;
                                                } else
                                                    Toast.makeText(LoginActivity.this, "Password Wrong", Toast.LENGTH_SHORT).show();
                                            } else
                                                Toast.makeText(LoginActivity.this, "Please Register", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    Intent intent = new Intent(LoginActivity.this, Home.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
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

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(LoginActivity.this);
        alertdialog.setTitle("Network Status");
        alertdialog.setMessage("We were unable to detect a network connection. A network connection is required to use this Application");
        alertdialog.setNeutralButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });

        alertdialog.show();

    }

}
