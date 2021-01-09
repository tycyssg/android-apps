package com.example.stories;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stories.models.Story;
import com.example.stories.models.StoryEvent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ViewStoryActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RecyclerView eventsList;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView.Adapter adapter;
    private FirebaseAuth firebaseAuth;
    private String selectedDocumentId;
    private GoogleMap gMap;
    private List<StoryEvent> storyEventsForMaps = new ArrayList<>();
    private TextView viewStoryTitle, startLocationValue, endLocationValue, dateCreatedValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);
        Bundle bundle = getIntent().getExtras();
        selectedDocumentId = bundle.getString("documentId");
        run();
    }

    private void run() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        eventsList = findViewById(R.id.eventsList);
        firebaseAuth = FirebaseAuth.getInstance();

        viewStoryTitle = findViewById(R.id.viewStoryTitle);
        startLocationValue = findViewById(R.id.startLocationValue);
        endLocationValue = findViewById(R.id.endLocationValue);
        dateCreatedValue = findViewById(R.id.dateCreatedValue);
        getStoryData();
    }

    private void getMapData() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMapFragmentId);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        for (StoryEvent storyEvent : storyEventsForMaps) {
            gMap.addMarker(new MarkerOptions().position(new LatLng(
                    storyEvent.getLatitude(), storyEvent.getLongitude()
            )).title(storyEvent.getLocation()));
            gMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(storyEvent.getLatitude(), storyEvent.getLongitude())));
        }



    }

    private void getStoryData() {
        DocumentReference docRef = firebaseFirestore.collection("stories").document(selectedDocumentId);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Story story = documentSnapshot.toObject(Story.class);
                viewStoryTitle.setText(story.getStoryTitle().substring(0, 1).toUpperCase() + story.getStoryTitle().substring(1).toLowerCase());
                startLocationValue.setText(story.getLocationStart());
                endLocationValue.setText(story.getLocationEnd());
                dateCreatedValue.setText(format.format(story.getDateCreated()));

                setAdapterData(story.getStoryEventList());
                getMapData();
                storyEventsForMaps = story.getStoryEventList();
            }
        });

    }

    private void setAdapterData(List<StoryEvent> storyEventList) {
        adapter = new StoryAdapter(storyEventList);
        eventsList.setHasFixedSize(true);
        eventsList.setLayoutManager(new LinearLayoutManager(this));
        eventsList.setAdapter(adapter);
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
            case R.id.profileId:
                profile();
                break;
            case R.id.viewAllUsersId:
                viewAllUsers();
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

    private void profile() {
        startActivity(new Intent(getApplicationContext(), Profile.class));
    }

    private void viewAllUsers() {
        startActivity(new Intent(getApplicationContext(), ViewAllUsers.class));
    }

    private void dashboard() {
        startActivity(new Intent(getApplicationContext(), Dashboard.class));
    }

    public static class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        private List<StoryEvent> storyEvents;

        public StoryAdapter(List<StoryEvent> storyEvents) {
            this.storyEvents = storyEvents;
        }

        @NonNull
        @Override
        public StoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_event_row, parent, false);
            return new StoryAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StoryAdapter.ViewHolder holder, int position) {
            holder.titleRow.setText(storyEvents.get(position).getEventTitle());
            holder.locationRowValue.setText(storyEvents.get(position).getLocation());
            holder.dateRowValue.setText(format.format(storyEvents.get(position).getDateCreated()));
            holder.descriptionRow.setText(storyEvents.get(position).getEventDescription());
            Picasso.get().load(storyEvents.get(position).getPhotoUrl()).into(holder.imageRow);
        }

        @Override
        public int getItemCount() {
            return storyEvents.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleRow, locationRowValue, dateRowValue, descriptionRow;
            ImageView imageRow;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                titleRow = itemView.findViewById(R.id.titleRow);
                locationRowValue = itemView.findViewById(R.id.locationRowValue);
                dateRowValue = itemView.findViewById(R.id.dateRowValue);
                descriptionRow = itemView.findViewById(R.id.descriptionRow);
                imageRow = itemView.findViewById(R.id.imageRow);
            }
        }
    }
}