package com.matrawy.a7oda.Final_Project.data;

import android.provider.BaseColumns;

/**
 * Created by 7oda on 11/27/2016.
 */

public class Movie_Contract {
    public static final class MovieInfo implements BaseColumns  // A Table For Movie information
    {
               public static final String TABLE_NAME = "movie";
        public static final String IMG_MAIN = "img_main";
        public static final String IMG = "img";
        public static final String YEAR = "year";
        public static final String ID = "id";
        public static final String SCORE = "score";
        public static final String TITLE = "title";
        public static final String DETAIL = "detail";
        public static final String FAVORITE = "favorite";
        public static final String TRAILER_SOURCE = "trailer_source";
        public static final String REVIEW = "review";

    }
    public static final class SortInfo implements BaseColumns  //A Table for all Avaliable sort as top popular , where i save the sort type
        //and the id of af the sorted movie according to sort type -_-''
    {
        public static final String TABLE_NAME = "sorting";
        public static final String TYPE = "type";
        public static final String VALUE = "value";
    }

}
