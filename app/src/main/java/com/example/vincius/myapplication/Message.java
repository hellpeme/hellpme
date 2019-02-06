package com.example.vincius.myapplication;

public class Message {

    private String text;
    private long timestamp;
    private String fromId;
    private String toId;
    private String photoUrl;

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFromId() {
        return fromId;
    }

    public String getToId() {
        return toId;
    }



}
