package com.example.chatappadd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText inputEmail, inputPassword;
    Button loginButton1, createButton1;

    FirebaseAuth mAuth;
    ProgressDialog mLoadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail=findViewById(R.id.inputEmail);
        inputPassword=findViewById(R.id.inputPassword);
        loginButton1=findViewById(R.id.loginButton);
        createButton1=findViewById(R.id.createButton);

        mAuth = FirebaseAuth.getInstance();
        mLoadingBar = new ProgressDialog(this);

        createButton1.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loginButton1.setOnClickListener(view -> LoginButton());
    }

    private void LoginButton() {
        String email = Objects.requireNonNull(inputEmail.getText()).toString();
        String pass = Objects.requireNonNull(inputPassword.getText()).toString();

        if(email.isEmpty() || !email.contains("@")){
            showError(inputEmail, "Email is not valid!");
        }  else if (pass.isEmpty() || pass.length()<5){
            showError(inputPassword, "Password mast be more then 5 characters");
        }  else {
            mLoadingBar.setTitle("Login");
            mLoadingBar.setMessage("Please wait!");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mLoadingBar.dismiss();
                    Toast.makeText(getApplicationContext(), "Login is Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, SetupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    mLoadingBar.dismiss();
                    Toast.makeText(getApplicationContext(), "Login has Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void showError(TextInputEditText til, String s) {
        til.setError(s);
        til.requestFocus();
    }


}