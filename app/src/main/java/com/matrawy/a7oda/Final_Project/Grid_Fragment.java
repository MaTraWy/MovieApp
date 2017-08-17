package com.matrawy.a7oda.Final_Project;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.matrawy.a7oda.Final_Project.data.DB;
import com.matrawy.a7oda.stageone.R;

import java.util.ArrayList;

import static com.matrawy.a7oda.stageone.R.id.gridview;
public class Grid_Fragment extends Fragment{
    MovieListner movie_listner;
    public  Image_Adapter CustomAdapter;
    DB db ;
    SQLiteDatabase db_heleper ;
    ArrayList<Movie> Movie_List = new ArrayList<>();
    ArrayList<Movie> Fav_list = new ArrayList<>();
    public static boolean flag_popular=false;
    ProgressDialog dialog;
    AlertDialog.Builder alertDialogBuilder;

    public static boolean flag_top_rated=false;
    public Grid_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View Root = inflater.inflate(R.layout.activity_fragment_grid_, container, false);
        GridView grid = (GridView) Root.findViewById(gridview);

        dialog = new ProgressDialog(getActivity());
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        db = new DB(getActivity());
        db_heleper = db.getReadableDatabase();
        CustomAdapter = new Image_Adapter(getActivity(), new ArrayList<Movie>());
        grid.setAdapter(CustomAdapter);

        if (savedInstanceState != null) {
            Movie_List = (ArrayList<Movie>) savedInstanceState.getSerializable("movie_list");
            Save_Fave(); //save the favourite in an array
            update_Adapter(); //update the adapter

        } else if (checkInternet() && (savedInstanceState == null) && !flag_top_rated) {
            update_DataBase(); //update all movies as there is internet
        }
        else {
            alertDialogBuilder.setTitle("Hey ^_^");
            alertDialogBuilder.setMessage("We Will reterive Data from Data Base");
            alertDialogBuilder.show();
            Get_From_Data_Base(); //reteriving data from database
        }
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                movie_listner.setselectedmovie(CustomAdapter.getItem(position));
            }
        });
        return Root;
    }

    private void update_DataBase() {
        if(!checkInternet()) { //recheck internet before getting the data
            Get_From_Data_Base();
            return;
        }
        dialog.setMessage("Retreving data From Cloud ...");
        dialog.setCancelable(false);
        dialog.show();
        Get_From_Data_Base(); //save current Favourite in the database if exists before getting new one from internet.
        db.Trunclate(db_heleper); //free the database to get new data
        Movie_ImageAndData_Parse Async_Popular = new Movie_ImageAndData_Parse("popular", this,getActivity().getApplicationContext()); //getting data
        Async_Popular.execute("popular");
    }

    public void Continue()
    {
        if(!flag_popular) {
            alertDialogBuilder.setMessage("Error Happens int getting popular - sorted movies from cloud");
            alertDialogBuilder.show();
            return;
        }


        if(!flag_top_rated) {
            alertDialogBuilder.setMessage("Error Happens int getting top rated - sorted movies from cloud");
            alertDialogBuilder.show();
            return;
        }

        if(!Fav_list.isEmpty()) //setting favourite to the new movies if there is exists and old favourite movie
            for(Movie m :Fav_list)
            {
                db.Set_Favourite(m.getId(),db_heleper);
            }
        Get_From_Data_Base(); //after finishing , update the movie list with new information
        dialog.dismiss();

    }
    public void Get_From_Data_Base()
    {
        if(db.Database_rows(db_heleper)==0)
        {
            alertDialogBuilder.setMessage("No Elements in DataBase - get internet connection");
            alertDialogBuilder.show();
            return;
        }
        Movie_List.clear(); // to refull it
        Cursor m=db.get_all_Movie(db_heleper);
        while(m.moveToNext())
        {
            Movie run = new Movie(m.getInt(4),m.getString(2),m.getString(1),m.getString(3),m.getString(5),m.getString(6),m.getString(7));
            String x[];
            ArrayList<String>Trailer_source = new ArrayList<>();
            ArrayList<String>Reviews = new ArrayList<>();

            x= m.getString(9).split(",");
            for(String current:x)
                Trailer_source.add(current);
            run.setTrailer_Source(Trailer_source);

            x=m.getString(10).split(",");
            for(String current:x)
                Reviews.add(current);
            run.setReview(Reviews);

            int is_favorite =m.getInt(8);
            if(is_favorite==0)
                run.setFavourite(false);
            else
                run.setFavourite(true);
            Movie_List.add(run);
        }
        m.close();
        Save_Fave(); //save favourite in favourite list
        update_Adapter(); //update the adapter
    }

    public void update_Adapter() //to update adapter according to preferences
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String type = prefs.getString(getString(R.string.pref_type_key), getString(R.string.pref_type_default));
        ArrayList<Movie> Sorted_List = new ArrayList<>();
        switch(type)
        {
            case"popular":{
                Sorted_List=db.get_sort(db_heleper,Movie_List,"popular");
            }break;
            case"top_rated":{
                Sorted_List=db.get_sort(db_heleper,Movie_List,"top_rated");
            }break;
            case"all":{
                Sorted_List=Movie_List;
            }break;
            //case"Favourite":{Sorted_List=db.get_all_fav(db_heleper,Movie_List);}break;
            case"Favourite":{
                Sorted_List=Fav_list;
            }break;
        }
        if(Sorted_List==null)
            return;

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Movie List - "+type);
        if(!CustomAdapter.isEmpty())
            CustomAdapter.clear();
        for(Movie m:Sorted_List) {
            CustomAdapter.add(m);
        }
    }
    public void Save_Fave()
    {
        if(!Fav_list.isEmpty())
            Fav_list.clear();
        for(Movie m:Movie_List)
            if(m.isFavourite())
                Fav_list.add(m);
        /*Cursor Query_result = db.get_fav(db_heleper);
        if(Query_result.getCount()==0)  return;
        while (Query_result.moveToNext())
            Fav_list.add()*/
    }
    boolean checkInternet()  //check internet function
    {
        if(internet_connection.isOnline())
            return true;
        else
        {
            alertDialogBuilder.setTitle("No Internet Connection, We will try to read from database");
            alertDialogBuilder.show();
            return false;
        }

    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("movie_list", Movie_List);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db_heleper.close();
        db.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(flag_top_rated)
            Get_From_Data_Base();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.action_refesh) {
            update_DataBase();
            return true;
        }
        return true;
    }

    public MovieListner getMovie_listner() {
        return movie_listner;
    }

    public void setMovie_listner(MovieListner movie_listner) {
        this.movie_listner = movie_listner;
    }
}
