package com.matrawy.a7oda.Final_Project;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Movie_Review_Parse extends AsyncTask<String, Void, Void> {
    private Movie current_movie;
    private ArrayAdapter<String> Current_Adapter;
    private Detail_Fragment activity;

    Movie_Review_Parse(Movie current_movie, ArrayAdapter<String> Current_Adapter,Detail_Fragment activity) {
        this.current_movie = current_movie;
        this.Current_Adapter = Current_Adapter;
        this.activity=activity;
    }

    private static final String LOG_TAG = Movie_Review_Parse.class.getSimpleName();

    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String Review_Jason = "";
        try {
            if (!internet_connection.isOnline()) {
                return null;
            }
            URL url = new URL("http://api.themoviedb.org/3/movie/" + current_movie.getId() + "/reviews?api_key=7460b289fc4f6a569bdd2805296a01e7");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

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
            Review_Jason = buffer.toString();

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
                Get_review_Url(Review_Jason);
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void Get_review_Url(String string) throws JSONException {
        JSONObject reviewJASON = new JSONObject(string);
        JSONArray review_result = reviewJASON.getJSONArray("results");
        if(review_result.length()!=0)
        {
            current_movie.getReview().clear();

            for (int i = 0; i < review_result.length(); i++) {
                JSONObject Num = review_result.getJSONObject(i);
                if (Num.getString("author") != null) ;
                {
                    current_movie.getReview().add((Num.getString("author") + " Said: " + Num.getString("content")));
                }
            }
        }
        else
            current_movie.getReview().add("No Data in exsists in cloud");
        return;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        Current_Adapter.clear();
        for(int i=0;i<current_movie.getReview().size();i++)
        {
            Current_Adapter.add(current_movie.getReview().get(i));
        }
        activity.continuee(); //resum the main thread
        activity.upload_Trailer_Review();
    }
}
