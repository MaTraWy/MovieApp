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

/**
 * Created by 7oda on 11/25/2016.
 */

public class Movie_Trailers_Parse extends AsyncTask<String, Void, Void> {
    Movie current_movie;
    Trailer_Adapter trailer_adapter;
    ArrayAdapter<String> review_adapter;
    Detail_Fragment activity;

    Movie_Trailers_Parse(Movie current_movie, Trailer_Adapter Current_Adapter,ArrayAdapter<String> review_adapter,Detail_Fragment activity) {
        this.current_movie = current_movie;
        this.trailer_adapter = Current_Adapter;
        this.review_adapter=review_adapter;
        this.activity=activity;
    }

    private static final String LOG_TAG = Movie_Trailers_Parse.class.getSimpleName();

    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String Trailer_Jason = "";
        try {
            if (!internet_connection.isOnline()) {
                return null;
            }
            URL url = new URL("http://api.themoviedb.org/3/movie/" + current_movie.getId() + "/trailers?api_key=7460b289fc4f6a569bdd2805296a01e7");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Read the input stream into a String
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
            Trailer_Jason = buffer.toString();

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
                Get_Trailer_Url(Trailer_Jason);
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Void Get_Trailer_Url(String string) throws JSONException {
        JSONObject TrailerJASON = new JSONObject(string);
        JSONArray trailer_result = TrailerJASON.getJSONArray("youtube");
        if (trailer_result.length()!=0) {
            current_movie.getTrailer_Source().clear();
            for (int i = 0; i < trailer_result.length(); i++) {
                JSONObject Num = trailer_result.getJSONObject(i);
                if (Num.getString("name") != null) ;
                {
                    current_movie.getTrailer_Source().add(Num.getString("source"));
                }
            }
        }
            else
            {
                current_movie.getTrailer_Source().add("No Data in exsists in cloud");
            }
        return null;
    }

        @Override
        protected void onPostExecute(Void aVoid) {
            //if (current_movie.get_trailer_string().equalsIgnoreCase("no Trailer"))
              //  return;
            trailer_adapter.clear();
            for(int i=0;i<current_movie.getTrailer_Source().size();i++)
            {
                String s = "Trailer #" + (i + 1);
                trailer_adapter.add(s);
            }
            if(internet_connection.isOnline()) {
                Movie_Review_Parse Review_Task = new Movie_Review_Parse(current_movie, review_adapter, activity); //getting the Review
                Review_Task.execute();
            }
        }
    }
