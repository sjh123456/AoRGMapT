package com.AoRGMapT.bean;

import android.graphics.Bitmap;

public class ImageBean {


    public ImageBean(String image, Bitmap bitmap, int type) {
        this.image = image;
        this.bitmap = bitmap;
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private String image;

    private Bitmap bitmap;

    //0 正常  1 添加按钮
    private int type;
}
