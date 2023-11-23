package com.example.study;

public class StudyItem {
    private String title;
    private String description;

    public StudyItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
