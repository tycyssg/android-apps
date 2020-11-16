package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notes.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private static final String TAG = "";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private User userProfile;
    private ImageView profileImage;
    private TextView profileTopText, profileTopTextSecondary;
    private EditText profileEditName, profileEditPhone, profileEditPhotoUrl;
    private Button editProfileId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        run();
    }

    private void run() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        initializeComponents();
        getProfileData();
        onEditProfile();
    }

    private void initializeComponents() {
        progressBar = findViewById(R.id.progressBarProfile);
        profileImage = findViewById(R.id.profileAvatarId);
        profileTopText = findViewById(R.id.profileTopText);
        profileTopTextSecondary = findViewById(R.id.profileTopTextSecondary);
        profileEditName = findViewById(R.id.profileEditName);
        profileEditPhone = findViewById(R.id.profileEditPhone);
        profileEditPhotoUrl = findViewById(R.id.profileEditPhotoUrl);
        editProfileId = findViewById(R.id.editProfileId);
    }

    private void getProfileData() {
        final String userId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference docRef = firebaseFirestore.collection("users").document(userId);
        showProgressBar();
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        userProfile = new User(data.get("fullName").toString(), data.get("phone").toString(), data.get("photoUrl").toString());
                        setupProfile(userProfile);
                        setProfileDataInForms(userProfile);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
                hideProgressBar();
            }
        });
    }

    private void onEditProfile() {
        editProfileId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressBar();
                final String userId = firebaseAuth.getCurrentUser().getUid();


                if (TextUtils.isEmpty(profileEditName.getText())) {
                    Toast.makeText(Profile.this, "Error! Name is required", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(profileEditPhotoUrl.getText())) {
                    Toast.makeText(Profile.this, "Error! some word for the avatar is required", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(profileEditPhone.getText())) {
                    Toast.makeText(Profile.this, "Error! Phone is required", Toast.LENGTH_LONG).show();
                    return;
                }

                User user = new User(profileEditName.getText().toString(), profileEditPhone.getText().toString(), "https://robohash.org/" + profileEditPhotoUrl.getText().toString().trim() + "?set=set3");
                DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);
                documentReference.set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Profile.this, "Profile successfully created!", Toast.LENGTH_LONG).show();
                        getProfileData();
                    }
                });

                hideProgressBar();
            }
        });
    }

    private void setupProfile(User userProfile) {
        Picasso.get().load(userProfile.getPhotoUrl()).into(profileImage);
        profileTopText.setText(userProfile.getFullName() + " profile");

        final SimpleDateFormat format = new SimpleDateFormat("E:M:y HH:mm:ss");
        long dateCreated = firebaseAuth.getCurrentUser().getMetadata().getLastSignInTimestamp();
        profileTopTextSecondary.setText("Last Sign In: " + format.format(dateCreated));
    }

    private void setProfileDataInForms(User userProfile) {
        profileEditName.setText(userProfile.getFullName());
        profileEditPhone.setText(userProfile.getPhone());

        String[] urlSplitBySlash = userProfile.getPhotoUrl().split("/");
        String[] urlSplitByQuestionMark = urlSplitBySlash[urlSplitBySlash.length - 1].split("\\?");

        profileEditPhotoUrl.setText(urlSplitByQuestionMark[0]);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.dashboardId:
                dashboard();
                break;
            case R.id.logOutId:
                logout();
                break;
        }

        return true;
    }

    private void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void dashboard() {
        startActivity(new Intent(getApplicationContext(), Dashboard.class));
    }
}