package com.team47.fabflix;

import java.util.ArrayList;

public class Movie {
    private String title;
    private String year;
    private String director;
    private ArrayList<String> stars;
    private ArrayList<String> genres;

    public Movie (String title, String year, String director, ArrayList<String> stars, ArrayList<String> genres) {
        this.title = title;
        this.year = year;
        this.director = director;
        this.stars = stars;
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }
    public String getYear() {
        return year;
    }
    public String getDirector() {
        return director;
    }
    public ArrayList<String> getStars() {
        return stars;
    }
    public ArrayList<String> getGenres() {
        return genres;
    }

}
