package com.example.pawel.udemy_instagramclone.baseFunctions.mainGallery;


import android.graphics.Bitmap;

import java.util.List;

public class PhotoInfoObject {

    private String AuthorName;
    private String Description;
    private String ImageUrl;
    private String AvatarUrl;
    private String ID;
    private List<Comment> Comments;

    public PhotoInfoObject(String authorName, String description, String imageUrl, String avatarUrl, String ID, List<Comment> comments) {
        AuthorName = authorName;
        Description = description;
        ImageUrl = imageUrl;
        AvatarUrl = avatarUrl;
        this.ID = ID;
        Comments = comments;
    }

    public String getAuthorName() {

        return AuthorName;
    }

    public String getDescription() {

        return Description;
    }

    public String getImage() {

        return ImageUrl;
    }

    public String getAvatar() {

        return AvatarUrl;
    }

    public final String getID() {

        return ID;
    }

    public List<Comment> getComments() {

        return Comments;
    }
}
