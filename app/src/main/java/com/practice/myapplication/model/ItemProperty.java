package com.practice.myapplication.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ItemProperty extends RealmObject implements Comparable{

    @PrimaryKey
    private Integer id;
    private String title, imageUrl, description;
    private Boolean isFavorite;

    public ItemProperty(Integer id, String imageUrl, String title, String description, Boolean isFavorite){
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.isFavorite = isFavorite;
    }

    public ItemProperty() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public void setFavorite(Boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public int compareTo(Object o) {
        int compareage=((ItemProperty)o).getId();
        /* For Ascending order*/
        return this.id-compareage;

        /* For Descending order do like this */
//        return compareage-this.id;
    }
}
