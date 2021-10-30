package com.AoRGMapT.bean;

import android.graphics.Bitmap;

public class ImageBean {


    public ImageBean(String image, Bitmap bitmap, int type) {
        this.imagePath = image;
        this.bitmap = bitmap;
        this.type = type;
    }

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    private String imagePath;

    private Bitmap bitmap;

    //0 正常  1 添加按钮
    private int type;

    //云端返回的连接
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
