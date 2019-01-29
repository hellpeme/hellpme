package com.example.vincius.myapplication;

public class User {

    private  String uid;
    private  String username;
    private  String profileUrl;
    private  int pontos;

    public User(){

    }

    public User(String uid, String username, String profileUrl, int pontos) {
        this.uid = uid;
        this.username = username;
        this.profileUrl = profileUrl;
        this.pontos = pontos;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public int getPontos() {
        return pontos;
    }

}
