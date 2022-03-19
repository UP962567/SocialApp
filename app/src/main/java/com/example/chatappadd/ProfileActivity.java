package com.example.chatappadd;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView profileImage;
    EditText inputUsername1, inputFullName1, inputCountry1, inputNumber1;
    Button button;
    RadioGroup radioGrp1;
    RadioButton radioM1 , radioF1;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.PROFILEprofile_image);
        inputUsername1 = findViewById(R.id.PROFILEinputUsername);
        inputUsername1.setOnClickListener(view -> showError(inputUsername1, "Not allow to change!"));
        inputFullName1 = findViewById(R.id.PROFILEinputFullName);
        inputCountry1 = findViewById(R.id.PROFILEinputCountry);
        inputNumber1 = findViewById(R.id.PROFILEinputNumber);
        button = findViewById(R.id.PROFILEbuttonSetUp);
        radioM1 = findViewById(R.id.PROFILEradioM);
        radioF1 = findViewById(R.id.PROFILEradioF);
        radioGrp1 = findViewById(R.id.PROFILEradioGrp);



        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");


        mRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String profileImageURL = snapshot.child("profileImage").getValue().toString();
                    String country = snapshot.child("country").getValue().toString();
                    String phone = snapshot.child("phone").getValue().toString();
                    String username = snapshot.child("username").getValue().toString();
                    String fullname = snapshot.child("fullName").getValue().toString();
                    String gender = snapshot.child("gender").getValue().toString();

                    Picasso.get().load(profileImageURL).into(profileImage);
                    inputCountry1.setText(country);
                    inputNumber1.setText(phone);
                    inputUsername1.setText(username);
                    inputFullName1.setText(fullname);
                    if (gender == 2131231232+"") {
                        radioM1.setChecked(true);
                        radioF1.setChecked(false);
                    } else {
                        radioM1.setChecked(false);
                        radioF1.setChecked(true);
                    }


                } else {
                    Toast.makeText(ProfileActivity.this, "Data do not exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showError(EditText til, String s) {
        til.setError(s);
        til.requestFocus();
    }
}