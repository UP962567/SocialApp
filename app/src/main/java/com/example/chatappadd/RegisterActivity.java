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

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressDialog mLoadingBar;


    TextInputEditText inputEmail, inputPassword, inputRePassword;
    Button createButtonR, loginButtonR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mLoadingBar = new ProgressDialog(this);

        inputEmail=findViewById(R.id.inputEmailR);
        inputPassword=findViewById(R.id.inputPasswordR);
        inputRePassword=findViewById(R.id.inputRePasswordR);
        createButtonR=findViewById(R.id.createButtonR);
        loginButtonR=findViewById(R.id.loginButtonR);


        createButtonR.setOnClickListener(view -> RegistrationCall());

        loginButtonR.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void RegistrationCall() {
        String email = Objects.requireNonNull(inputEmail.getText()).toString();
        String pass = Objects.requireNonNull(inputPassword.getText()).toString();
        String repass = Objects.requireNonNull(inputRePassword.getText()).toString();
        
        if(email.isEmpty() || !email.contains("@")){
            showError(inputEmail, "Email is not valid!");
        }  else if (pass.isEmpty() || pass.length()<5){
            showError(inputPassword, "Password mast be more then 5 characters");
        } else if (!repass.equals(pass)){
            showError(inputRePassword, "Password did not match!");
        } else {
            mLoadingBar.setTitle("Registration");
            mLoadingBar.setMessage("Please wait!");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mLoadingBar.dismiss();
                    Toast.makeText(getApplicationContext(), "Registration is Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    mLoadingBar.dismiss();
                    Toast.makeText(getApplicationContext(), "Registration is Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void showError(TextInputEditText til, String s) {
        til.setError(s);
        til.requestFocus();
    }
}