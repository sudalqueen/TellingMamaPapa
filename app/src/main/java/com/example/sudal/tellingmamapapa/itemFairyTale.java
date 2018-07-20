package com.example.sudal.tellingmamapapa;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class itemFairyTale implements Serializable{
    private int image;
    private String title;
    private String explane;
    private int itemViewType;

    public itemFairyTale(){

    }

    public itemFairyTale(int image, String title, String explane) {
        this.image = image;
        this.title = title;
        this.explane = explane;
    }


    public int getImage() {
        return image;
    }

    public void setImage(int image) {
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

    public void setItemViewType(int itemViewType){
        this.itemViewType = itemViewType;
    }

    public int getItemViewType() {
        return itemViewType;
    }
}
