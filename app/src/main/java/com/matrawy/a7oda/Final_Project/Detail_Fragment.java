package com.matrawy.a7oda.Final_Project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.matrawy.a7oda.Final_Project.data.DB;
import com.matrawy.a7oda.stageone.R;

import java.util.ArrayList;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by 7oda on 11/30/2016.
 */

public class Detail_Fragment extends Fragment {
    private ListView Trailer_View;
    private  ListView Review_View;
    private Movie Current_Movie;
    private ArrayAdapter<String> reviewAdapter;
    private Trailer_Adapter trailerAdapter;
    private View Root;
    private Grid_Fragment grid_instance;
    private Switch Fav_Switch;
    private DB db;
    SQLiteDatabase db_heleper;
    int pixels;
    ProgressDialog progress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Root = inflater.inflate(R.layout.detail_fragment, container, false);
        Bundle current = getArguments();
        if(savedInstanceState!=null)
            Current_Movie = (Movie) savedInstanceState.getSerializable("movie");
        else
            Current_Movie = (Movie) current.getSerializable("object");
        //Log.e("current_movie_detail",Current_Movie.get_trailer_string());
        db = new DB(getActivity());
        db_heleper=db.getReadableDatabase();

        trailerAdapter = new Trailer_Adapter(getActivity(),new ArrayList<String>()); //trailer adapters ..
        Trailer_View = (ListView) Root.findViewById((R.id.list_Trailers));
        Trailer_View.setAdapter(trailerAdapter);

        reviewAdapter = new ArrayAdapter<String>(getActivity(),R.layout.reviewtheme,R.id.list_item_review,new ArrayList<String>()); // review adapters
        Review_View = (ListView) Root.findViewById(R.id.List_Review);
        Review_View.setAdapter(reviewAdapter);

        progress = new ProgressDialog(getActivity());
        progress.setCancelable(false);

        Fav_Switch = ((Switch) Root.findViewById(R.id.Favourite));
        if(Current_Movie.isFavourite())
            Fav_Switch.setChecked(true);
        Fav_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.Set_Favourite(Current_Movie.getId(),db_heleper);
                } else {
                    db.Remove_Favourite(Current_Movie.getId(),db_heleper);

                }
            }
        });

        final float scale = getActivity().getResources().getDisplayMetrics().density; //to auto fit trailers list according to its content
        pixels = (int) (60 * scale + 0.5f);

        intialView();
        Trailer_View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               /* //String m=mForecastAdapter.getItem(position);
                Context context = getActivity().getApplicationContext();
                CharSequence text = ((TextView) view).getText();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();*/
                Log.e("Posrtion",position+"           "+               Current_Movie.getTrailer_Source().size());
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+Current_Movie.getTrailer_Source().get(position))));


            }
        });
        return Root;
    }
    private void intialView()
    {
        ImageView Pic = (ImageView) Root.findViewById(R.id.Detail_image);
        TextView year = (TextView) Root.findViewById(R.id.Year);
        TextView Score = (TextView) Root.findViewById(R.id.Score);
        TextView detail = (TextView) Root.findViewById(R.id.content_detail);
        TextView Title = (TextView) Root.findViewById(R.id.title);
        year.setText(Current_Movie.getYear());
        Score.setText(Current_Movie.getScore());
        detail.setText(Current_Movie.getDetail());
        Title.setText(Current_Movie.getTitle());
        Picasso.with(getActivity().getApplicationContext()).load(Current_Movie.getImg()).into(Pic);
        trailerAdapter.clear();
        reviewAdapter.clear();
        if(Current_Movie.get_review_string().equalsIgnoreCase("No review")&&
                Current_Movie.get_trailer_string().equalsIgnoreCase("No Trailer")) {
            if(internet_connection.isOnline()) {
                progress.setMessage("Reteriving data ...");
                progress.show();
                Movie_Trailers_Parse Trailer_Async = new Movie_Trailers_Parse(Current_Movie, trailerAdapter, reviewAdapter, this);
                Trailer_Async.execute();
            }
            else
            {
                if(Current_Movie.get_trailer_string().equalsIgnoreCase("No Trailer"))
                    trailerAdapter.add("No information exsists in data base");
                if(Current_Movie.get_review_string().equalsIgnoreCase("No review"))
                    reviewAdapter.add("No information exsists in data base");
            }

        }
        else //reteriving data from database
        {
            for(int i=0;i<Current_Movie.getTrailer_Source().size();i++)
                trailerAdapter.add("Trailer #" + (i + 1));
            for(String current_movie:Current_Movie.getReview())
                reviewAdapter.add(current_movie);
            Trailer_View.setLayoutParams(new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, Current_Movie.getTrailer_Source().size()*pixels));        //continuee();
            //AUTO fit the trailer list layout
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void upload_Trailer_Review()//this one to chache the trailers and review that got from internet..
    {
        db.add_trialer_review(Current_Movie.getId(),db_heleper,Current_Movie.get_trailer_string(),Current_Movie.get_review_string());
    }

    public void continuee() //this function called when Async task done its job
    {
        if(Current_Movie.get_review_string().equalsIgnoreCase("No Data in exsists in cloud,")) {
            Review_View.setLayoutParams(new LinearLayout.LayoutParams((AbsListView.LayoutParams.MATCH_PARENT),15*pixels));
        }
        if(Current_Movie.get_trailer_string().equalsIgnoreCase("No Data in exsists in cloud,")){
            Trailer_View.setLayoutParams(new LinearLayout.LayoutParams((AbsListView.LayoutParams.MATCH_PARENT),15*pixels));
        }
        else
            Trailer_View.setLayoutParams(new LinearLayout.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, Current_Movie.getTrailer_Source().size()*pixels));        //continuee();
        progress.dismiss();
    }

    @Override
    public void onStop() {
        super.onStop();
        db.close();
        db_heleper.close();
        if(getArguments().getBoolean("type")) //in case of Two pane Ui before changing the movie it will store trailers and review. where type is type of ui
            grid_instance.Get_From_Data_Base();
        super.onStop();
    }

    public void setGrid_instance(Grid_Fragment grid_instance) {
        this.grid_instance = grid_instance;
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("movie", Current_Movie); //puting the saved instance state
    }
}