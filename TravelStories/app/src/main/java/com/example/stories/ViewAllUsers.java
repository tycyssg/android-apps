package com.example.stories;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stories.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

public class ViewAllUsers extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_users);
        run();
    }

    private void run() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userList = findViewById(R.id.viewUsersLisId);
        getUsersData();
    }

    private void getUsersData() {
        Query query = firebaseFirestore.collection("users");

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = firebaseAdapter(options);

        userList.setHasFixedSize(true);
        userList.setLayoutManager(new LinearLayoutManager(this));
        userList.setAdapter(adapter);

    }

    private FirestoreRecyclerAdapter<User, ViewAllUsers.ListViewHolder> firebaseAdapter(FirestoreRecyclerOptions<User> options) {

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        return new FirestoreRecyclerAdapter<User, ViewAllUsers.ListViewHolder>(options) {

            @NonNull
            @Override
            public ViewAllUsers.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_user_row, parent, false);
                return new ViewAllUsers.ListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewAllUsers.ListViewHolder holder, int position, @NonNull User userProfile) {
                holder.viewUserFullName.setText(userProfile.getFullName());
                holder.viewUserPhone.setText(userProfile.getPhone());
                holder.userEmailId.setText(userProfile.getEmail());
                Picasso.get().load(userProfile.getPhotoUrl()).into(holder.userViewAvatar);
                holder.userId = userProfile.getUserId();
                holder.position = position;
            }

        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView viewUserFullName, viewUserPhone, userEmailId;
        ImageView userViewAvatar;
        Button viewStoriesId;
        String userId;
        int position;

        public ListViewHolder(@NonNull final View itemView) {
            super(itemView);
            viewUserFullName = itemView.findViewById(R.id.viewUserFullName);
            viewUserPhone = itemView.findViewById(R.id.viewUserPhone);
            userEmailId = itemView.findViewById(R.id.userEmailId);
            userViewAvatar = itemView.findViewById(R.id.userViewAvatar);

            viewStoriesId = itemView.findViewById(R.id.viewStoriesId);


            viewStoriesId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ViewAllUserStories.class);
                    intent.putExtra("selectedUserId", userId);
                    startActivity(intent);
                }
            });
        }
    }
}