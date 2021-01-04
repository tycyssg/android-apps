package com.example.stories;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ViewAllUserStories extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_user_stories);

        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("selectedUserId");
        System.out.println(message + "here is the message");
    }
}