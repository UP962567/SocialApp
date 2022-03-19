package com.example.chatappadd;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatappadd.backcode.Posts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    StorageReference sRef;
    DatabaseReference mRef,pRef, lRef;
    ProgressDialog mBar;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Posts,MyViewHolder> adapter;
    FirebaseRecyclerOptions<Posts> options;

    private final int REQUEST_CODE = 101;
    Uri imageUri;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    String profileImageUrlV, userNameV;

    TextView usernameHeader1;
    CircleImageView ProfileImage_Header1;


    EditText inputAddPost1;
    ImageView addPostImageView1, sendPostImageView1;
    NavigationView navView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        pRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        lRef = FirebaseDatabase.getInstance().getReference().child("Liked");
        mBar = new ProgressDialog(this);
        sRef = FirebaseStorage.getInstance().getReference().child("PostImages");
        recyclerView=findViewById(R.id.recycleViewPost);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar=findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chat App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        addPostImageView1=findViewById(R.id.addPostImageView);
        sendPostImageView1=findViewById(R.id.sendPostImageView);
        inputAddPost1=findViewById(R.id.inputAddPost);

        drawerLayout=findViewById(R.id.drawerLayout);
        navigationView=findViewById(R.id.navView);

        View view = navigationView.inflateHeaderView(R.layout.drawer_header);
        ProfileImage_Header1=view.findViewById(R.id.ProfileImage_Header);
        usernameHeader1=view.findViewById(R.id.username_header);

        navigationView.setNavigationItemSelectedListener(this);
        sendPostImageView1.setOnClickListener(view12 -> AddPost());
        addPostImageView1.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,REQUEST_CODE);
        });

        LoadPosts();
    }

    private void LoadPosts() {
        options= new FirebaseRecyclerOptions.Builder<Posts>().setQuery(pRef,Posts.class).build();
        adapter= new FirebaseRecyclerAdapter<Posts, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Posts model) {
                String postKey = getRef(position).getKey();
                holder.postDesc.setText(model.getPostText());
                final String timeAgo = calculateTimeAgo(model.getDate());
                holder._profileTimePosted.setText(timeAgo);
                holder.username.setText(model.getUsernameID());
                Picasso.get().load(model.getImagePost()).into(holder.postImage);
                Picasso.get().load(model.getUserProfileImage()).into(holder.profileImage);
                holder.countLikes(postKey,mUser.getUid(), lRef);


                holder.likeImage.setOnClickListener(view -> lRef.child(postKey).child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            lRef.child(postKey).child(mUser.getUid()).removeValue();
                            holder.likeImage.setColorFilter(Color.GRAY);
                            notifyDataSetChanged();
                        } else {
                            lRef.child(postKey).child(mUser.getUid()).setValue("like");
                            holder.likeImage.setColorFilter(Color.GREEN);
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Error in like!", Toast.LENGTH_SHORT).show();
                    }
                }));

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_posting, parent, false);
                return new MyViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private String calculateTimeAgo(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        try {
            long time = sdf.parse(date).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==REQUEST_CODE && resultCode == RESULT_OK && data!=null) {
            imageUri=data.getData();
            addPostImageView1.setImageURI(imageUri);
        }
    }

    private void AddPost() {
        String postText= inputAddPost1.getText().toString();
        if(postText.isEmpty()) {
            inputAddPost1.setError("Please Write something!");
        } else if (addPostImageView1 == null) {
            Toast.makeText(this, "please add an image!", Toast.LENGTH_SHORT).show();
        } else {
            mBar.setTitle("Posting it!");
            mBar.setCanceledOnTouchOutside(false);
            mBar.show();

            Date date = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            String strDate = formatter.format(date);

            sRef.child(mUser.getUid() + " - "+strDate + " - " + userNameV).putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    sRef.child(mUser.getUid() + " - "+strDate + " - " + userNameV).getDownloadUrl().addOnSuccessListener(uri -> {
                        HashMap hashMap = new HashMap();
                        hashMap.put("date", strDate);
                        hashMap.put("imagePost", uri.toString());
                        hashMap.put("postText",postText);
                        hashMap.put("userProfileImage", profileImageUrlV);
                        hashMap.put("usernameID", userNameV);
                        pRef.child(strDate + " - " + userNameV).updateChildren(hashMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                mBar.dismiss();
                                Toast.makeText(MainActivity.this, "Post Added!", Toast.LENGTH_SHORT).show();
                                addPostImageView1.setImageResource(R.drawable.ic_image);
                                inputAddPost1.setText(null);
                            } else {
                                Toast.makeText(MainActivity.this, "Error Posting!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                }
                else {
                    mBar.dismiss();
                    Toast.makeText(MainActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.user_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.user_profile:
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.user_friends:
                Toast.makeText(this, "Friends", Toast.LENGTH_SHORT).show();
                break;
            case R.id.user_addfriend:
                Toast.makeText(this, "Add Friends", Toast.LENGTH_SHORT).show();
                break;
            case R.id.user_chat:
                Toast.makeText(this, "Chat", Toast.LENGTH_SHORT).show();
                break;
            case R.id.user_logout:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mUser==null){
            SendUserToLoginActivity();
        } else {
            mRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        profileImageUrlV=snapshot.child("profileImage").getValue().toString();
                        userNameV=snapshot.child("username").getValue().toString();
                        Picasso.get().load(profileImageUrlV).into(ProfileImage_Header1);

                        usernameHeader1.setText(userNameV);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Sorry! We had a problem!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void SendUserToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return true;
    }
}