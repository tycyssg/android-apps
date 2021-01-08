package com.example.stories;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stories.models.Story;
import com.example.stories.models.StoryActions;
import com.example.stories.models.StoryEvent;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import lombok.Data;


@Data
public class Dashboard extends AppCompatActivity {

    private static final String TAG = "";
    private static final int SELECTED = 1;
    private FirestoreRecyclerAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference, imageRef;
    private UploadTask uploadTask;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private RecyclerView displayList;
    private SearchView searchView;
    private EditText storyTitle, eventTitle, locationStart, locationEnd, eventLocation, eventDescription;
    private Uri uriImage;
    private double currentEventLat;
    private double currentEventLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        run();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.searchIcon);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Stories");
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

    private void run() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        initializeComponents();
        onAddStories();
        getStoriesData();
    }

    private void initializeComponents() {
        fab = findViewById(R.id.addStoriesId);
        progressBar = findViewById(R.id.progressBarDashboard);
        displayList = findViewById(R.id.storiesDisplayList);
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
        final String userId = firebaseAuth.getCurrentUser().getUid();

        Query query = firebaseFirestore.collection("stories").whereEqualTo("userId", userId).orderBy("searchKey").startAfter(s).endAt(s + "\uf8ff");

        FirestoreRecyclerOptions<Story> options = new FirestoreRecyclerOptions.Builder<Story>()
                .setQuery(query, Story.class)
                .build();

        adapter = firebaseAdapter(options, userId);
        adapter.startListening();
        displayList.setAdapter(adapter);
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

    private void onAddStories() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        storyTitle = dialogView.findViewById(R.id.storyTitle);
        locationStart = dialogView.findViewById(R.id.locationStart);
        locationEnd = dialogView.findViewById(R.id.locationEnd);

        getPlaces(locationStart, locationEnd);

        Button cancelStory = dialogView.findViewById(R.id.cancelStoryDialog);
        Button addStory = dialogView.findViewById(R.id.addStoryDialogId);

        cancelStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });

        addStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(storyTitle.getText())) {
                    Toast.makeText(Dashboard.this, "Error! A title is required", Toast.LENGTH_LONG).show();
                    return;
                }

                onSaveStory(new Story(storyTitle.getText().toString(), locationStart.getText().toString(), locationEnd.getText().toString()));
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void showEventDialog(final DocumentReference documentReference, final Story story) {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.event_custom_dialog, null);

        eventTitle = dialogView.findViewById(R.id.eventTitle);
        eventLocation = dialogView.findViewById(R.id.eventLocation);
        eventDescription = dialogView.findViewById(R.id.eventDescription);

        getEventPlaces(eventLocation);

        Button uploadPhoto = dialogView.findViewById(R.id.addPicture);
        Button cancelEvent = dialogView.findViewById(R.id.cancelEventDialog);
        Button addEvent = dialogView.findViewById(R.id.addEventDialog);

        cancelEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, SELECTED);
            }
        });


        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(eventTitle.getText())) {
                    Toast.makeText(Dashboard.this, "Error! A title is required", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(eventLocation.getText())) {
                    Toast.makeText(Dashboard.this, "Error! A location is required", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(eventDescription.getText())) {
                    Toast.makeText(Dashboard.this, "Error! A description is required", Toast.LENGTH_LONG).show();
                    return;
                }

                StoryEvent event = new StoryEvent(eventTitle.getText().toString(), eventLocation.getText().toString(), eventDescription.getText().toString());
                event.setLatitude(currentEventLat);
                event.setLongitude(currentEventLng);
                uploadImage(dialogBuilder, documentReference, story, event);

            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void uploadImage(final AlertDialog dialogBuilder, final DocumentReference documentReference, final Story story, final StoryEvent storyEvent) {
        imageRef = storageReference.child("images/" + UUID.randomUUID());
        uploadTask = imageRef.putFile(uriImage);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Photo Upload Fail", Toast.LENGTH_SHORT).show();
                dialogBuilder.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        storyEvent.setPhotoUrl(uri.toString());
                        List<StoryEvent> eventList = new ArrayList<>();

                        if (story.getStoryEventList() != null) {
                            eventList = story.getStoryEventList();
                        }

                        eventList.add(storyEvent);
                        story.setStoryEventList(eventList);
                        onSaveEvent(dialogBuilder, documentReference, story);
                    }
                });
            }
        });


    }

    private void onSaveEvent(final AlertDialog dialogBuilder, DocumentReference documentReference, Story story) {
        documentReference.set(story, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Story successfully created!", Toast.LENGTH_LONG).show();
                dialogBuilder.dismiss();
            }
        });
    }

    private void getPlaces(EditText locationStart, EditText locationEnd) {
        Places.initialize(getApplicationContext(), "AIzaSyCENMd7pHPxl4TgjHIzIkf1pJPJyEOezdo");
        locationStart.setFocusable(false);
        locationEnd.setFocusable(false);

        locationStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(Dashboard.this);
                startActivityForResult(intent, 100);
            }
        });

        locationEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(Dashboard.this);
                startActivityForResult(intent, 200);
            }
        });

    }

    private void getEventPlaces(EditText eventLocation) {
        Places.initialize(getApplicationContext(), "AIzaSyCENMd7pHPxl4TgjHIzIkf1pJPJyEOezdo");
        eventLocation.setFocusable(false);

        eventLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(Dashboard.this);
                startActivityForResult(intent, 300);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            locationStart.setText(place.getAddress());
        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            locationEnd.setText(place.getAddress());
        } else if (requestCode == 300 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            currentEventLat = Objects.requireNonNull(place.getLatLng()).latitude;
            currentEventLng = place.getLatLng().longitude;
            eventLocation.setText(place.getAddress());
        } else if (requestCode == 1 && resultCode == RESULT_OK) {
            uriImage = data.getData();
        } else if (requestCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onSaveStory(Story story) {
        showProgressBar();
        final String userId = firebaseAuth.getCurrentUser().getUid();
        story.setUserId(userId);

        CollectionReference colRef = firebaseFirestore.collection("stories");
        colRef.add(story).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Dashboard.this, "Story successfully created!", Toast.LENGTH_LONG).show();
            }
        });

        hideProgressBar();
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void getStoriesData() {

        showProgressBar();
        final String userId = firebaseAuth.getCurrentUser().getUid();
        Query query = firebaseFirestore.collection("stories").whereEqualTo("userId", userId);

        FirestoreRecyclerOptions<Story> options = new FirestoreRecyclerOptions.Builder<Story>()
                .setQuery(query, Story.class)
                .build();
        hideProgressBar();

        adapter = firebaseAdapter(options, userId);

        displayList.setHasFixedSize(true);
        displayList.setLayoutManager(new LinearLayoutManager(this));
        displayList.setAdapter(adapter);

    }

    private FirestoreRecyclerAdapter<Story, ListViewHolder> firebaseAdapter(FirestoreRecyclerOptions<Story> options, final String userId) {

        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return new FirestoreRecyclerAdapter<Story, ListViewHolder>(options) {
            @NonNull
            @Override
            public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
                return new ListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ListViewHolder holder, int position, @NonNull Story story) {
                holder.noteTitle.setText(story.getStoryTitle());
                holder.locationStartText.setText(story.getLocationStart());
                holder.locationEndText.setText(story.getLocationEnd());
                holder.storyDate.setText(format.format(story.getDateCreated()));
                holder.userId = userId;
                holder.position = position;
            }

        };
    }

    private void getDocumentIdOfSelectedStory(String userId, final int somePos, final StoryActions storyActions) {
        firebaseFirestore.collection("stories").whereEqualTo("userId", userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                    if (i == somePos) {
                        String documentId = queryDocumentSnapshots.getDocuments().get(i).getId();
                        if (storyActions == StoryActions.DELETE) {
                            deleteData(documentId);
                        } else if (storyActions == StoryActions.VIEW) {
                            getSpecificStoryFromList(documentId);
                        }
                    }
                }
            }
        });

    }

    private void deleteData(String documentId) {
        firebaseFirestore.collection("stories").document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Dashboard.this, "Story successfully deleted!", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
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

    public void getSpecificStoryFromList(String docId) {
        final DocumentReference documentReference = firebaseFirestore.collection("stories").document(docId);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Story s = documentSnapshot.toObject(Story.class);
                updateStory(documentReference, s);
            }
        });

    }

    public void updateStory(DocumentReference documentReference, Story story) {
        showEventDialog(documentReference, story);
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, storyDate, locationStartText, locationEndText;
        Button deleteStory, viewStory;
        String userId;
        int position;

        public ListViewHolder(@NonNull final View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.storyTitleText);
            storyDate = itemView.findViewById(R.id.storyDateText);
            locationStartText = itemView.findViewById(R.id.locationStartText);
            locationEndText = itemView.findViewById(R.id.locationEndText);
            deleteStory = itemView.findViewById(R.id.deleteStoryId);
            viewStory = itemView.findViewById(R.id.viewStoryId);

            deleteStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDocumentIdOfSelectedStory(userId, position, StoryActions.DELETE);
                }
            });

            viewStory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDocumentIdOfSelectedStory(userId, position, StoryActions.VIEW);
                }
            });
        }
    }
}


