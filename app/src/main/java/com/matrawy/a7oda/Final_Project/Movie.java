package com.matrawy.a7oda.Final_Project;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by 7oda on 10/21/2016.
 */

public class Movie implements Serializable {
    private String img_main;
    private String img;
    private String year;
    private String time;
    private int id;
    boolean Favourite;
    private String Score;
    private String title;
    private String detail;
    private ArrayList<String> Trailer_Source;
    private ArrayList<String> Review;
    //private String
    public Movie(int id,String img,String img_url_home, String year, String Score, String title , String detail)
    {
        this.img_main = img_url_home;
        this.id=id;
        this.img= img;
        this.detail =detail;
        this.year = year;
        this.Score = Score;
        this.title = title;
        Trailer_Source = new ArrayList<>();
        Review = new ArrayList<>();
        Favourite = false;
    }

    public boolean isFavourite() {
        return Favourite;
    }

    public void setFavourite(boolean favourite) {
        Favourite = favourite;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getTrailer_Source() {
        return Trailer_Source;
    }

    public void setTrailer_Source(ArrayList<String> trailer_Source) {
        this.Trailer_Source = trailer_Source;
    }

    public ArrayList<String> getReview() {
        return Review;
    }

    public void setReview(ArrayList<String> review) {
        this.Review = review;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg_main() {
        return img_main;
    }

    public void setImg_main(String img_main) {
        this.img_main = img_main;
    }
    public String get_trailer_string()

    {
        if (Trailer_Source.isEmpty()||Trailer_Source.get(0).equalsIgnoreCase("No Trailer"))
            return "No Trailer";
        String string = "";
        for (String m : Trailer_Source)
            string += m + ",";
        return string;
    }
    public String get_review_string()

        {
        if (Review.isEmpty()||Review.get(0).equalsIgnoreCase("No review"))
        return "No review";
        String string = "";
        for (String m : Review)
        string += m + ",";
        return string;
        }
}
