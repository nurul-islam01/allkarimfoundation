package com.nit.allkarimfoundation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    
    private FirebaseAuth mAuth;

    private EditText emailET, passwordET;
    private SweetAlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        alertDialog = new SweetAlertDialog(this);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if( currentUser != null && currentUser.getUid() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void login(View view) {

        try {
            String email = emailET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();

            if(TextUtils.isEmpty(email)) {
                emailET.setError("Please enter email");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passwordET.setError("Please enter password");
                return;
            }

            loginWith(email, password);


        }catch (Exception e) {
            Log.d(TAG, "login: " + e.getMessage());
        }

    }

    private void loginWith(String email, String password) {

        alertDialog.setTitle("Processing...");
        alertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        alertDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            alertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            alertDialog.dismissWithAnimation();
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            alertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            alertDialog.setTitle("Login Filed");
                            alertDialog.setContentText("Authentication fail, Please try again");
                            alertDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    alertDialog.dismissWithAnimation();
                                }
                            });
                            return;
                        }

                        // ...
                    }
                });

    }
}