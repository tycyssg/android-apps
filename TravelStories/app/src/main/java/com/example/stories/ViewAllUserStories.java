package com.example.stories;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stories.models.Story;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;

public class ViewAllUserStories extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;
    private RecyclerView userStoriesList;
    private String selectedUserId;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_user_stories);

        Bundle bundle = getIntent().getExtras();
        selectedUserId = bundle.getString("selectedUserId");
        run();
    }


    private void run() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userStoriesList = findViewById(R.id.viewUserStoriesList);
        getUsersData();
    }

    private void getUsersData() {
        Query query = firebaseFirestore.collection("stories").whereEqualTo("userId", selectedUserId);

        FirestoreRecyclerOptions<Story> options = new FirestoreRecyclerOptions.Builder<Story>()
                .setQuery(query, Story.class)
                .build();

        adapter = firebaseAdapter(options);

        userStoriesList.setHasFixedSize(true);
        userStoriesList.setLayoutManager(new LinearLayoutManager(this));
        userStoriesList.setAdapter(adapter);

    }

    private FirestoreRecyclerAdapter<Story, ViewAllUserStories.ListViewHolder> firebaseAdapter(FirestoreRecyclerOptions<Story> options) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        return new FirestoreRecyclerAdapter<Story, ViewAllUserStories.ListViewHolder>(options) {

            @NonNull
            @Override
            public ViewAllUserStories.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_user_story_row, parent, false);
                return new ViewAllUserStories.ListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewAllUserStories.ListViewHolder holder, int position, @NonNull Story story) {
                holder.documentId = getSnapshots().getSnapshot(position).getId();
                holder.viewUserStoryTitle.setText(story.getStoryTitle());
                holder.viewUserStoryFromDestination.setText(story.getLocationStart());
                holder.viewUserStoryToDestination.setText(story.getLocationEnd());
                holder.viewUserStoryDate.setText(format.format(story.getDateCreated()));
                holder.userId = story.getUserId();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchIcon);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Stories by start destination");
        prepareSearch();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.dashboardId:
                dashboard();
                break;
            case R.id.profileId:
                profile();
                break;
            case R.id.viewUsersId:
                viewAllUsers();
                break;
            case R.id.logOutId:
                logout();
                break;
        }

        return true;
    }

    private void prepareSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                processSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                processSearch(newText);
                return true;
            }
        });
    }

    private void processSearch(String s) {
        s = s.toLowerCase();
        Query query = firebaseFirestore.collection("stories").whereEqualTo("userId", selectedUserId).orderBy("searchKey").startAfter(s).endAt(s + "\uf8ff");

        FirestoreRecyclerOptions<Story> options = new FirestoreRecyclerOptions.Builder<Story>()
                .setQuery(query, Story.class)
                .build();

        adapter = firebaseAdapter(options);
        adapter.startListening();
        userStoriesList.setAdapter(adapter);
    }

    private void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void profile() {
        startActivity(new Intent(getApplicationContext(), Profile.class));
    }

    private void dashboard() {
        startActivity(new Intent(getApplicationContext(), Dashboard.class));
    }

    private void viewAllUsers() {
        startActivity(new Intent(getApplicationContext(), ViewAllUsers.class));
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView viewUserStoryTitle, viewUserStoryFromDestination, viewUserStoryToDestination, viewUserStoryDate;
        Button viewFullStoryId;
        String userId;
        String documentId;
        int position;

        public ListViewHolder(@NonNull final View itemView) {
            super(itemView);
            viewUserStoryTitle = itemView.findViewById(R.id.viewUserStoryTitle);
            viewUserStoryFromDestination = itemView.findViewById(R.id.viewUserStoryFromDestination);
            viewUserStoryToDestination = itemView.findViewById(R.id.viewUserStoryToDestination);
            viewUserStoryDate = itemView.findViewById(R.id.viewUserStoryDate);
            viewFullStoryId = itemView.findViewById(R.id.viewFullStoryId);

            viewFullStoryId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ViewStoryActivity.class);
                    intent.putExtra("documentId", documentId);
                    startActivity(intent);
                }
            });
        }
    }
}