package com.hfad.budgetin.model;

public class User {
    public String myUsername,myEmail, myLocation;
    public User(){}
    public User(String myUsername, String myEmail, String myLocation){
        this.myUsername = myUsername;
        this.myEmail = myEmail;
        this.myLocation = myLocation;
    }

    public String getMyUsername() {
        return myUsername;
    }

    public void setMyUsername(String myUsername) {
        this.myUsername = myUsername;
    }

    public String getMyEmail() {
        return myEmail;
    }

    public void setMyEmail(String myEmail) {
        this.myEmail = myEmail;
    }

    public String getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(String myLocation) {
        this.myLocation = myLocation;
    }
}
