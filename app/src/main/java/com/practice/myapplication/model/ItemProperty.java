package com.practice.myapplication.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ItemProperty extends RealmObject implements Comparable{

    @PrimaryKey
    private Integer id;
    private String title, imageUrl, description, releaseDate, voteAverage;
    private Boolean isFavorite;

    public ItemProperty(Integer id, String imageUrl, String title, String description, String releaseDate, String voteAverage, Boolean isFavorite){
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.isFavorite = isFavorite;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
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

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
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
