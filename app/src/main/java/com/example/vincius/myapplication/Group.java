package com.example.vincius.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class Group implements Parcelable {

    private String groupName;
    private String uid;
    private String profileUrl;
    private String adminUser;
    private int maxUsers;
    private int currentNumUsers;
    private HashMap<String,String> listIDUser;

    public Group(){

    }

    public Group(String groupName, String uid, String profileUrl, String adminUser, int numberUsers) {
        this.groupName = groupName;
        this.uid = uid;
        this.profileUrl = profileUrl;
        this.adminUser = adminUser;
        listIDUser = new HashMap<>();
        maxUsers = numberUsers;
        currentNumUsers = 0;
    }

    protected Group(Parcel in) {
        uid = in.readString();
        groupName = in.readString();
        profileUrl = in.readString();
        adminUser = in.readString();
        listIDUser = (HashMap<String, String>) in.readSerializable();
        currentNumUsers = in.readInt();
        maxUsers = in.readInt();
    }


    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };

    public String getGroupName() {
        return groupName;
    }

    public String getUid() {
        return uid;
    }

    public String getProfileUrl() {
        return profileUrl;
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



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(groupName);
        dest.writeString(profileUrl);
        dest.writeString(adminUser);
        dest.writeSerializable(listIDUser);
        dest.writeInt(currentNumUsers);
        dest.writeInt(maxUsers);
    }
}
