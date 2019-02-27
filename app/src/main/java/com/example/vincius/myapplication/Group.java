package com.example.vincius.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Group implements Parcelable {

    private String groupName;
    private String uid;
    private String profileUrl;
    private String adminUser;

    //private List<User> listUser;

    public Group(){

    }

    public Group(String groupName, String uid, String profileUrl, String adminUser) {
        this.groupName = groupName;
        this.uid = uid;
        this.profileUrl = profileUrl;
        this.adminUser = adminUser;
    }

    protected Group(Parcel in) {
        uid = in.readString();
        groupName = in.readString();
        profileUrl = in.readString();
        adminUser = in.readString();
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

   // public List<User> getListUser() {
   //     return listUser;
    //}

    //public void setListUser(User user) {
      //  listUser.add(user);
    //}


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
    }
}
