package com.example.stories.models;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Story {
    private String storyTitle;
    private String locationStart;
    private String locationEnd;
    private Date dateCreated;
    private String userId;
    private String searchKey;

    public Story(String storyTitle, String locationStart, String locationEnd) {
        this.storyTitle = storyTitle;
        this.locationStart = locationStart;
        this.locationEnd = locationEnd;
        this.dateCreated = new Date();
        this.searchKey = locationStart.toLowerCase();
    }
}
