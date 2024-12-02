package com.example.lighthouseofmemory;

public class Alarm {
    private long timestamp;
    private String title;

    public Alarm(long timestamp, String title) {
        this.timestamp = timestamp;
        this.title = title;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Time: " + timestamp;
    }
}


