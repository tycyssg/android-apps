package com.example.notes.models;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Note {
    private String noteTitle;
    private String noteBody;
    private Date dateCreated;
    private String userId;

    public Note(String noteTitle, String noteBody) {
        this.noteTitle = noteTitle;
        this.noteBody = noteBody;
        this.dateCreated = new Date();
    }
}
