package com.example.vincius.myapplication.Fragments;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class ContactGroup implements Parcelable {
    private String uid;
    private String username;
    private long timestamp;
    private String photoUrl;
    private String lastMessage;
    private String adminUser;
    private int maxUsers;
    private int currentNumUsers;
    private HashMap<String,String> listIDUser;

    public ContactGroup(Parcel in) {
        uid = in.readString();
        username = in.readString();
        timestamp = in.readLong();
        photoUrl = in.readString();
        lastMessage = in.readString();
        adminUser = in.readString();
        maxUsers = in.readInt();
        currentNumUsers = in.readInt();
        listIDUser = (HashMap<String, String>) in.readSerializable();
    }

    public static final Creator<ContactGroup> CREATOR = new Creator<ContactGroup>() {
        @Override
        public ContactGroup createFromParcel(Parcel in) {
            return new ContactGroup(in);
        }

        @Override
        public ContactGroup[] newArray(int size) {
            return new ContactGroup[size];
        }
    };

    public ContactGroup() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getAdminUser() {
        return adminUser;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public int getCurrentNumUsers() {
        return currentNumUsers;
    }

    public HashMap<String, String> getListIDUser() {
        return listIDUser;
    }

    public void setAdminUser(String adminUser) {
        this.adminUser = adminUser;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public void setCurrentNumUsers(int currentNumUsers) {
        this.currentNumUsers = currentNumUsers;
    }

    public void setListIDUser(HashMap<String, String> listIDUser) {
        this.listIDUser = listIDUser;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(username);
        dest.writeLong(timestamp);
        dest.writeString(photoUrl);
        dest.writeString(lastMessage);
        dest.writeString(adminUser);
        dest.writeSerializable(listIDUser);
        dest.writeInt(currentNumUsers);
        dest.writeInt(maxUsers);
    }
}
