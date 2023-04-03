package com.example.chessprodotype;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;

public class User {

    public final static int START_RANK = 750;
    private String userName;
    private String password;
    private String lastName;
    private String firstName;
    private String imageUri;
    private int rank;
    private ArrayList<String> friendsUserNames;

    public User(String userName, String password, String lName, String fName, String uri, int rank){
        this.userName = userName;
        this.password = password;
        this.lastName = lName;
        this.firstName = fName;
        this.imageUri = uri;
        this.rank = rank;
        this.friendsUserNames = new ArrayList<>();
    }

    public User(){
        this.friendsUserNames = new ArrayList<>();
    }


    public ArrayList<String> getFriendsUserNames() {
        return friendsUserNames;
    }

    public String getImageUri() {return imageUri;}
    public String getUserName() { return userName;}
    public String getPassword(){ return password;}
    public String getLastName() {return lastName;}

    public String getFirstName() {return firstName;}
    public int getRank() {
        return rank;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setFriendsUserNames(ArrayList<String> friendsUserNames) {
        if (friendsUserNames == null) this.friendsUserNames = new ArrayList<>();
        else this.friendsUserNames = friendsUserNames;
    }

    //gets user name and adding it to friends list
    public void addFriend(String userName){
        this.friendsUserNames.add(userName);
    }

    //gets user name and removing it from friends list
    public void removeFriend(String userName){
        this.friendsUserNames.remove(userName);
    }

    //gets opponent rank frm game and if needs to increase ro decrease
    //returning rank points to add or subtract according to rank difference from opponent
    private int getRankToChange(int opponentRank, boolean inc){
        int dif;
        if (inc) dif = opponentRank - this.rank;
        else dif = this.rank - opponentRank;
        if (dif < -50)
            return 2;
        else if (dif >= -50 && dif <= -15)
            return 5;
        else if (dif > -15 && dif <= 0)
            return 8;
        else if (dif > 0 && dif <= 15)
            return 10;
        else if (dif > 15 && dif <= 30)
            return 13;
        else return 15;
    }

    //gets opponent rank from game and increasing rank accordingly
    public void increaseRank(int opponentRank){
        this.rank += getRankToChange(opponentRank, true);
    }


    //gets opponent rank from game and decreasing rank accordingly
    public void decreaseRank(int opponentRank){
        if (this.rank > 200)
            this.rank -= getRankToChange(opponentRank, false);
    }

    //gets context on app and image view and putting profile image uri inside image view
    public void putMyImageIntoFrame(Context context, ImageView imageView){
        Uri userImageUri;
        if (this.imageUri != null) {
            userImageUri = Uri.parse(this.imageUri);
            Glide.with(context).load(userImageUri).into(imageView);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", lName='" + lastName + '\'' +
                ", fName='" + firstName + '\'' +
                '}';
    }
}
