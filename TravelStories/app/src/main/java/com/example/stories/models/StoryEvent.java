package com.example.stories.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoryEvent {
    private String eventTitle;
    private String location;
    private String eventDescription;
    private Date dateCreated;
    private String photoUrl;

    public StoryEvent(String eventTitle, String location, String eventDescription) {
        this.eventTitle = eventTitle;
        this.location = location;
        this.eventDescription = eventDescription;
        this.dateCreated = new Date();
    }
}
