package com.example.myapplication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("messageRes")
    @Expose
    private String messageRes;

    private String name;
    private String email;
    private String age;
    private String downloadUrl;

    public User(String name, String email, String age, String downloadUrl) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.downloadUrl = downloadUrl;
    }

    public String getName() {
        return name;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    /**
     *
     * @return
     * The username
     */
    public String getMessageRes() {
        return messageRes;
    }

    /**
     *
     * @param messageRes
     * The Username
     */
    public void setMessageRes(String messageRes) {
        this.messageRes = messageRes;
    }

}
