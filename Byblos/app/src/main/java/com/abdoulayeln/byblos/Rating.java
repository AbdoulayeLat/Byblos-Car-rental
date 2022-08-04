package com.abdoulayeln.byblos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;


public class Rating implements Serializable {

    private String id,userName, branchName, comments, rating;

    public Rating(){}

    public Rating(String id, String userName, String branchName, String comments, String rating){
        this.userName = userName;
        this.branchName = branchName;
        this.comments = comments;
        this.rating = rating;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String input){
        this.id = input;
    }

    public String getUserName(){
        return this.userName;
    }

    public void setUserName(String input){
        this.userName = input;
    }

    public String getBranchName(){
        return this.branchName;
    }

    public void setBranchName(String input){
        this.branchName = input;
    }

    public String getComments(){
        return this.comments;
    }

    public void setComments(String input){
        this.comments = input;
    }

    public String getRating() {
        return this.rating;
    }

    public void setRating(String input){
        this.rating = input;
    }
}
