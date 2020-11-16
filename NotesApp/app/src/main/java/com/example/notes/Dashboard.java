package com.example.notes;

import android.app.AlertDialog;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.models.Note;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.List;

import lombok.Data;


@Data
public class Dashboard extends AppCompatActivity {

    private static final String TAG = "";
    FirestoreRecyclerAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private RecyclerView displayList;
    private List<Note> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        run();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.profileId:
                profile();
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
        initializeComponents();
        onAddNotes();
        getNotesData();
    }

    private void initializeComponents() {
        fab = findViewById(R.id.addNotesId);
        progressBar = findViewById(R.id.progressBarDashboard);
        displayList = findViewById(R.id.notesDisplayList);
    }

    private void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void profile() {
        startActivity(new Intent(getApplicationContext(), Profile.class));
    }

    private void onAddNotes() {
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

        final EditText noteTitle = dialogView.findViewById(R.id.noteTitle);
        final EditText noteBody = dialogView.findViewById(R.id.noteBody);

        Button cancelNote = dialogView.findViewById(R.id.cancelNodeDialog);
        Button addNote = dialogView.findViewById(R.id.addNoteDialogId);

        cancelNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(noteTitle.getText())) {
                    Toast.makeText(Dashboard.this, "Error! A title is required", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(noteBody.getText())) {
                    Toast.makeText(Dashboard.this, "Error! A note body is required", Toast.LENGTH_LONG).show();
                    return;
                }


                onSaveNotes(new Note(noteTitle.getText().toString(), noteBody.getText().toString()));
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void onSaveNotes(Note note) {
        showProgressBar();
        final String userId = firebaseAuth.getCurrentUser().getUid();
        note.setUserId(userId);

        CollectionReference colRef = firebaseFirestore.collection("notes");
        colRef.add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Dashboard.this, "Note successfully created!", Toast.LENGTH_LONG).show();
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

    private void getNotesData() {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        showProgressBar();
        final String userId = firebaseAuth.getCurrentUser().getUid();
        Query query = firebaseFirestore.collection("notes").whereEqualTo("userId", userId);

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        hideProgressBar();

        adapter = new FirestoreRecyclerAdapter<Note, ListViewHolder>(options) {
            @NonNull
            @Override
            public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
                return new ListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ListViewHolder holder, int position, @NonNull Note note) {
                holder.noteTitle.setText(note.getNoteTitle());
                holder.noteDate.setText(format.format(note.getDateCreated()));
                holder.noteBody.setText(note.getNoteBody());
                holder.userId = userId;
                holder.position = position;
            }

        };

        displayList.setHasFixedSize(true);
        displayList.setLayoutManager(new LinearLayoutManager(this));
        displayList.setAdapter(adapter);

    }

    private void getDataToDeleteNote(String userId, final int somePos) {
        firebaseFirestore.collection("notes").whereEqualTo("userId", userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                    if (i == somePos) {
                        String documentId = queryDocumentSnapshots.getDocuments().get(i).getId();
                        deleteData(documentId);
                    }
                }
            }
        });
    }

    private void deleteData(String documentId) {
        firebaseFirestore.collection("notes").document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Dashboard.this, "Note successfully deleted!", Toast.LENGTH_LONG).show();
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

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteDate, noteBody;
        Button deleteNote;
        String userId;
        int position;

        public ListViewHolder(@NonNull final View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitleText);
            noteDate = itemView.findViewById(R.id.noteDateText);
            noteBody = itemView.findViewById(R.id.noteBodyText);
            deleteNote = itemView.findViewById(R.id.deleteNoteId);

            deleteNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDataToDeleteNote(userId, position);
                }
            });
        }
    }
}


