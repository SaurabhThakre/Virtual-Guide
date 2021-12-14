package com.virtual_guide.virtualguide;

import com.google.firebase.firestore.Exclude;

public class Note {
    private String documentId = "";
    private String user = "";
    private String date = "";
    private String title = "";
    private String imgTitle = "";
    private String imageUrl = "";
    private String desc = "";
    private String landmark = "";
    private int liked = 0;

    public Note() {
        // needed
    }

    public Note(String user, String date, String title, String imgTitle, String imageUrl, String desc, String landmark, int liked) {
        this.user = user;
        this.date = date;
        this.title = title;
        this.imgTitle = imgTitle;
        this.imageUrl = imageUrl;
        this.desc = desc;
        this.landmark = landmark;
        this.liked = liked;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getImgTitle() {
        return imgTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public String getLandmark() {
        return landmark;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }
}
