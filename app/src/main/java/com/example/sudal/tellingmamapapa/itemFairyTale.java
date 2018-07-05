package com.example.sudal.tellingmamapapa;

import android.graphics.drawable.Drawable;

public class itemFairyTale {
    private Drawable image;
    private String title;
    private String explane;

    public itemFairyTale(Drawable image, String title) {
        this.image = image;
        this.title = title;
    }

    public itemFairyTale(Drawable image, String title, String explane) {
        this.image = image;
        this.title = title;
        this.explane = explane;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExplane() {
        return explane;
    }

    public void setExplane(String explane) {
        this.explane = explane;
    }
}