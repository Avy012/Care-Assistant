package com.example.lighthouseofmemory;


public class Alarm {
    private String title;
    private long timestamp;
    private int amount;

    public Alarm(String title, long timestamp, int amount) {
        this.title = title;
        this.timestamp = timestamp;
        this.amount = amount;
    }

    // 약부분
    public Alarm(String title, long timestamp) {
        this.title = title;
        this.timestamp = timestamp;
        this.amount = -1;
    }


    public String getTitle() {
        return title;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "title='" + title + '\'' +
                ", timestamp=" + timestamp +
                ", amount=" + amount +
                '}';
    }
}

