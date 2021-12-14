package com.virtual_guide.virtualguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText emailId;
    TextInputEditText passwordId;
    Button login, signup;
    FirebaseAuth mFirebaseAuth;
    ProgressBar progressBar;
    String email;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.email);
        passwordId = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        progressBar = findViewById(R.id.progressbar);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

                if (mFirebaseUser != null) {
                    if (mFirebaseUser.isEmailVerified()){
                        Toast.makeText(MainActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                        String id = mFirebaseUser.getUid();
                        String user = mFirebaseUser.getEmail();
                        Intent intoHome = new Intent(getApplicationContext(), FirstPage.class);
                        intoHome.putExtra("id", id);
                        intoHome.putExtra("user", user);
                        startActivity(intoHome);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                }

            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailId.getText().toString();
                String password = passwordId.getText().toString();

                if (email.isEmpty()) {
                    emailId.setError("Please enter Email Id");
                    emailId.requestFocus();
                } else if (password.isEmpty()) {
                    passwordId.setError("Please enter Password");
                    passwordId.requestFocus();
                } else if (!(email.isEmpty() && password.isEmpty())) {
                    progressBar.setVisibility(View.VISIBLE);
                    mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                                if (mFirebaseUser.isEmailVerified()){
                                        Toast.makeText(MainActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                                    String id = mFirebaseUser.getUid();
                                    String user = mFirebaseUser.getEmail();
                                    Intent intoHome = new Intent(getApplicationContext(), FirstPage.class);
                                    intoHome.putExtra("id", id);
                                    intoHome.putExtra("user", user);
                                        startActivity(intoHome);
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "Your Email not verified!, Please check your email for verification link.", Toast.LENGTH_LONG).show();
                                    }
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Login Error, Please Login Again or Sign Up if does not have account", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Signup.class));
            }
        });
    }

    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

}
