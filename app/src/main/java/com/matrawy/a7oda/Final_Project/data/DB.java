package com.matrawy.a7oda.Final_Project.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.matrawy.a7oda.Final_Project.Movie;

import java.util.ArrayList;

/**
 * Created by 7oda on 11/26/2016.
 */

public class DB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1; //for now i fixed the version for 1

    static final String DATABASE_NAME = "movie.db";

    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + Movie_Contract.MovieInfo.TABLE_NAME + " (" +
                Movie_Contract.MovieInfo._ID + " INTEGER PRIMARY KEY," +
                Movie_Contract.MovieInfo.IMG_MAIN + " TEXT NOT NULL, " +
                Movie_Contract.MovieInfo.IMG + " TEXT NOT NULL, " +
                Movie_Contract.MovieInfo.YEAR + " TEXT NOT NULL, " +
                Movie_Contract.MovieInfo.ID + " INTEGER UNIQUE NOT NULL, " +
                Movie_Contract.MovieInfo.SCORE + " TEXT NOT NULL, " +
                Movie_Contract.MovieInfo.TITLE + " TEXT NOT NULL, " +
                Movie_Contract.MovieInfo.DETAIL + " TEXT NOT NULL, " +
                Movie_Contract.MovieInfo.FAVORITE + " TEXT NOT NULL, " +
                Movie_Contract.MovieInfo.TRAILER_SOURCE + " TEXT NOT NULL, " +
                Movie_Contract.MovieInfo.REVIEW + " TEXT NOT NULL" +
                " );";
        final String SQL_CREATE_SORT_TABLE = "CREATE TABLE " + Movie_Contract.SortInfo.TABLE_NAME + " (" +
                Movie_Contract.SortInfo._ID + " INTEGER PRIMARY KEY," +
                Movie_Contract.SortInfo.TYPE + " TEXT NOT NULL, " +
                Movie_Contract.SortInfo.VALUE + " TEXT NOT NULL " +
                " );";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_SORT_TABLE);
    }

    public void addmovie(Movie Add, SQLiteDatabase db) {
            if(Get_Movie_Id(db,Add.getId())!=0) //because some time when i parse movie according to top popular or top rated there exists a movie in both do i d'ont want to repeat movies
                return;
            ContentValues Contentvalues = new ContentValues();
            Contentvalues.put(Movie_Contract.MovieInfo.IMG_MAIN, Add.getImg_main());
            Contentvalues.put(Movie_Contract.MovieInfo.IMG, Add.getImg());
            Contentvalues.put(Movie_Contract.MovieInfo.YEAR, Add.getYear());
            Contentvalues.put(Movie_Contract.MovieInfo.ID, Add.getId());
            Contentvalues.put(Movie_Contract.MovieInfo.SCORE, Add.getScore());
            Contentvalues.put(Movie_Contract.MovieInfo.TITLE, Add.getTitle());
            Contentvalues.put(Movie_Contract.MovieInfo.DETAIL, Add.getDetail());
            Contentvalues.put(Movie_Contract.MovieInfo.FAVORITE, Add.isFavourite());
            Contentvalues.put(Movie_Contract.MovieInfo.TRAILER_SOURCE, Add.get_trailer_string());
            Contentvalues.put(Movie_Contract.MovieInfo.REVIEW, Add.get_review_string());
            db.insert(Movie_Contract.MovieInfo.TABLE_NAME, null, Contentvalues);
        }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Movie_Contract.MovieInfo.TABLE_NAME);
        onCreate(db);
    }

    public Cursor get_all_Movie(SQLiteDatabase db) {  //get all movies in database
        String table = Movie_Contract.MovieInfo.TABLE_NAME;
        String selection = Movie_Contract.MovieInfo.FAVORITE + "=?";
        String[] selectionArgs = {"0"};
        String groupBy = null;
        String having = null;
        Cursor  cursor = db.rawQuery("select * from "+Movie_Contract.MovieInfo.TABLE_NAME,null);
        return cursor;
    }

    public ArrayList<Movie> get_sort(SQLiteDatabase db, ArrayList<Movie> movie_list,String Sort_Type) { //get sort according to top_rated or popular
        String table = Movie_Contract.SortInfo.TABLE_NAME;
        String selection = Movie_Contract.SortInfo.TYPE + "=?";
        String[] selectionArgs = {Sort_Type};
        ArrayList<Movie> Sorted_Array = new ArrayList<>();
        Cursor sort_cursor = db.query(table, null, selection, selectionArgs, null, null, null, null);
        if (sort_cursor.getCount() == 0)
            return null;
        sort_cursor.moveToFirst();
        String Values = sort_cursor.getString(2);
        String Values1[] = Values.split(","); //i separate between movie id by ","
        for (String Values2 : Values1) {
            for (Movie current : movie_list)
                if ((current.getId() + "").equalsIgnoreCase(Values2))
                    Sorted_Array.add(current);
        }
        sort_cursor.close();
        return Sorted_Array;
    }
    public void Insert_Sort(SQLiteDatabase db,String Sort_Type,String Values) //insert a sort
    {
        ContentValues Contentvalues = new ContentValues();
        Contentvalues.put(Movie_Contract.SortInfo.TYPE,Sort_Type);
        Contentvalues.put(Movie_Contract.SortInfo.VALUE,Values);
        db.insert(Movie_Contract.SortInfo.TABLE_NAME, null, Contentvalues);
    }
    public void Trunclate(SQLiteDatabase db)  //delete all data in tables ..
    {
        db.execSQL("DROP TABLE IF EXISTS " + Movie_Contract.MovieInfo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Movie_Contract.SortInfo.TABLE_NAME);
        onCreate(db);
    }
    public void Set_Favourite(int ID,SQLiteDatabase db) //set favourite true..
    {
        ContentValues Contentvalues = new ContentValues();
        String table = Movie_Contract.MovieInfo.TABLE_NAME;
        Contentvalues.put(Movie_Contract.MovieInfo.FAVORITE,true);
        db.update(table,Contentvalues,Movie_Contract.MovieInfo.ID+" = "+ID,null);
    }
    public void Remove_Favourite(int ID,SQLiteDatabase db) //remove favourite
    {
        ContentValues Contentvalues = new ContentValues();
        String table = Movie_Contract.MovieInfo.TABLE_NAME;
        Contentvalues.put(Movie_Contract.MovieInfo.FAVORITE,false);
        db.update(table,Contentvalues,Movie_Contract.MovieInfo.ID+" = "+ID,null);
    }

    public void add_trialer_review(int ID,SQLiteDatabase db,String Trialer,String Review) //add trailer and review for cache purpose..
    {
        ContentValues Contentvalues = new ContentValues();
        String table = Movie_Contract.MovieInfo.TABLE_NAME;
        Contentvalues.put(Movie_Contract.MovieInfo.REVIEW,Review);
        Contentvalues.put(Movie_Contract.MovieInfo.TRAILER_SOURCE,Trialer);
        int x=db.update(table,Contentvalues,Movie_Contract.MovieInfo.ID+" = "+ID,null);
    }
    public int Database_rows(SQLiteDatabase db) //to get all rows in database
    {
        Cursor  cursor = db.rawQuery("select * from "+Movie_Contract.MovieInfo.TABLE_NAME,null);
        cursor.moveToFirst();
        int m=cursor.getCount();
        cursor.close();
        return m;
    }
    public int Get_Movie_Id(SQLiteDatabase db,int id) //to get id for a movie.. used to know if i will insert an already exists movie
    {
        Cursor  cursor = db.rawQuery("select * from "+Movie_Contract.MovieInfo.TABLE_NAME+" Where "+Movie_Contract.MovieInfo.ID+" = "+id,null);
        int x=cursor.getCount();
        cursor.close();
        return x;
    }
}