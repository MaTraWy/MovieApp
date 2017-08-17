package com.matrawy.a7oda.Final_Project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.matrawy.a7oda.Final_Project.data.DB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by 7oda on 10/21/2016.
 */

public class Movie_ImageAndData_Parse extends AsyncTask<String,Void, Void> {

    private static final String LOG_TAG = Movie_ImageAndData_Parse.class.getSimpleName();
    private Context context;
    private Image_Adapter l;
    private String sort_type;
    private String type;
    private ArrayList<Movie> movies;
    private Grid_Fragment Fragment;
    public Movie_ImageAndData_Parse(String type,Grid_Fragment Fragment,Context context) {
        this.type=type;
        this. Fragment= Fragment;
        movies = new ArrayList<>();
        this.context=context;
    }

    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String Img_Jason = "";
        sort_type = params[0];
        try {
            if (!internet_connection.isOnline()) {
                return null;
            }
            URL url = new URL("http://api.themoviedb.org/3/movie/" + params[0] + "?api_key=7460b289fc4f6a569bdd2805296a01e7");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // getting input stream
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            Img_Jason = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);

            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
            try {
                Get_Image_Url(Img_Jason);
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private ArrayList<Movie> Get_Image_Url(String string) throws JSONException {
        JSONObject IMGJASON = new JSONObject(string);
        JSONArray Img_result = IMGJASON.getJSONArray("results");
        String img_url, Overview, date, title, Score,img_url_home;
        int id;
        movies = new ArrayList<Movie>();

        for (int i = 0; i < Img_result.length(); i++) {
            JSONObject Num = Img_result.getJSONObject(i);
            if (Num.getString("poster_path") != null) ;
            {
                img_url_home = "http://image.tmdb.org/t/p/w342" + Num.getString("poster_path");
                img_url = "http://image.tmdb.org/t/p/w154" + Num.getString("poster_path");
                title = Num.getString("original_title");
                id = Num.getInt("id");
                Overview = Num.getString("overview");
                Score = Num.getString("vote_average");
                date = Num.getString("release_date");
                Movie meta = new Movie(id,img_url,img_url_home, date, Score, title, Overview);
                movies.add(meta);
            }
        }
        Database_insert(movies);
        return movies;
    }

    protected void Database_insert(ArrayList<Movie> images) {
        DB database = new DB(context);
        SQLiteDatabase DB_Helper = database.getReadableDatabase();
        String Values="";
        if (images == null) {
            CharSequence text = "Check Your Connection please.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        for(Movie currentmovie :images)
        {
            database.addmovie(currentmovie,DB_Helper);
            Values +=currentmovie.getId()+",";
        }
        database.Insert_Sort(DB_Helper,sort_type,Values);
        database.close();
        DB_Helper.close();
    }

    @Override
    protected void onPostExecute(Void sds) {
        if(type.equalsIgnoreCase("popular"))
        {
            Grid_Fragment.flag_popular = true;
            Movie_ImageAndData_Parse Async_Top_rated = new Movie_ImageAndData_Parse("top_rated", Fragment,context);
            Async_Top_rated.execute("top_rated");

        }

        else {
            Grid_Fragment.flag_top_rated = true;
            Fragment.Continue();
        }
    }

}