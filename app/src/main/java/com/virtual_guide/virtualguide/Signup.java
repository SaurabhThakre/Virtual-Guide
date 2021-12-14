package com.virtual_guide.virtualguide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signup extends AppCompatActivity {

    EditText emailId;
    TextInputEditText passwordId;
    Button login, signup;
    FirebaseAuth mFirebaseAuth;
    ProgressBar progressBar;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_signup);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.email);
        passwordId = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        progressBar = findViewById(R.id.progressbar);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailId.getText().toString();
                String password = passwordId.getText().toString();
                if(email.isEmpty()){
                    emailId.setError("Please enter Email Id");
                    emailId.requestFocus();
                }
                else if(password.isEmpty()){
                    passwordId.setError("Please enter Password");
                    passwordId.requestFocus();
                }
                else if(!(email.isEmpty() && password.isEmpty())){
                    progressBar.setVisibility(View.VISIBLE);
                    mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(Signup.this, "SignUp Unsuccessful, Please Try Again! Length of Password should be greater than 8",Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                            else {
                                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                                mFirebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(Signup.this, "We have sent you a Verification Link to your Email address", Toast.LENGTH_LONG).show();
                                    }
                                });
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intoMain = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intoMain);
                                    }
                                }, 5000);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(Signup.this, "Error Occurred!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signup.this, MainActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser!= null)
        {
            if (mFirebaseUser.isEmailVerified()){
                String id = mFirebaseUser.getUid();
                String user = mFirebaseUser.getEmail();
                Intent intoHome = new Intent(getApplicationContext(), FirstPage.class);
                intoHome.putExtra("id", id);
                intoHome.putExtra("user", user);
                startActivity(intoHome);
            }
        }
    }
}
