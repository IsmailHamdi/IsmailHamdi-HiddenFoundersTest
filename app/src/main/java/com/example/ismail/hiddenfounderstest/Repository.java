package com.example.ismail.hiddenfounderstest;

import android.graphics.Bitmap;
import android.icu.text.NumberFormat;

import java.text.DecimalFormat;

/**
 * Created by ismail on 02.01.2018.
 * Class containing all the necessary information of a github repository
 */

public class Repository {

    private String name, description, ownerName;
    private Bitmap ownerAvatar;
    private Long numberStars;
    private Long id;

    public  Repository(){}
    public Repository(String name, String description, String ownerName, Bitmap ownerAvatar, Long numberStars,Long id) {
        this.name = name;
        this.description = description;
        this.ownerName = ownerName;
        this.ownerAvatar = ownerAvatar;
        this.numberStars = numberStars;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Bitmap getOwnerAvatar() {
        return ownerAvatar;
    }

    public Float getNumberStars() {
        float res;
        DecimalFormat df = new DecimalFormat();
        res = (float) numberStars / 1000;
        df.setMaximumFractionDigits(2);
        return  Float.parseFloat(df.format(res));
    }

    public Long getId(){
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setOwnerAvatar(Bitmap ownerAvatar) {
        this.ownerAvatar = ownerAvatar;
    }

    public void setNumberStars(Long numberStars) {
        this.numberStars = numberStars;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
