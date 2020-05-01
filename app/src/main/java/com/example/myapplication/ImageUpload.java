package com.example.myapplication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageUpload {
    @SerializedName("downloadUrl")
    @Expose
    private String downloadUrl;

//    public ImageUpload(String downloadUrl) {
//        this.downloadUrl = downloadUrl;
//    }

    /**
     *
     * @return
     * The downloadUrl
     */
    public String getDownloadUrlRes() {
        return downloadUrl;
    }

    /**
     *
     * @param downloadUrl
     * The Username
     */
    public void setDownloadUrlRes(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
