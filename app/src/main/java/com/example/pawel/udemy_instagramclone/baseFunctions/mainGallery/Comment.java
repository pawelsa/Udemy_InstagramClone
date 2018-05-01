package com.example.pawel.udemy_instagramclone.baseFunctions.mainGallery;

/**
 * Created by Pawel on 25.02.2018.
 */

public class Comment {

    private final String comment;
    private final String username;
    private final boolean isComment;

    public Comment(String comment, String username) {
        this.comment = comment;
        this.username = username;
        this.isComment = true;
    }

    public Comment() {
        this.comment = null;
        this.username = null;
        this.isComment = false;
    }

    public String getComment() {
        return comment;
    }

    public String getUsername() {
        return username;
    }

    public boolean isComment() {
        return isComment;
    }
}
