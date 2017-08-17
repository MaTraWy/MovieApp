package com.matrawy.a7oda.Final_Project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.matrawy.a7oda.stageone.R;

public class MainActivity extends AppCompatActivity implements MovieListner  {
    private ProgressDialog dialog;
    boolean mIsTwoPane = false;
    Grid_Fragment First_Fragment;
    Detail_Fragment Second_Fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //adding image home icon to action bar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().show();

        //passing context to other class..
        internet_connection.Mycontext=getApplicationContext();

        //The two fragment in two pane ui
        First_Fragment = new Grid_Fragment();
        if(mIsTwoPane) {
            Second_Fragment = new Detail_Fragment();
        }
        if (savedInstanceState != null) {
            First_Fragment =(Grid_Fragment) getSupportFragmentManager().getFragment(savedInstanceState, "Fragment");
            First_Fragment.setMovie_listner(this);
            Second_Fragment = (Detail_Fragment) getSupportFragmentManager().getFragment(savedInstanceState, "Second_Fragment");
            if(Second_Fragment!=null)
                Second_Fragment.setGrid_instance(First_Fragment);
        }
        else{
            First_Fragment.setMovie_listner(this);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, First_Fragment,"Fragment")
                    .commit();

        }
        if (null != findViewById(R.id.Detail_Main)) {
            mIsTwoPane = true;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //shared preferance class..
        if (item.getItemId()==R.id.action_settings)
        {
            Intent m = new Intent(this,Setting.class);
            startActivity(m);
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //handling the activty destroyed ..
        getSupportFragmentManager().putFragment(outState, "Fragment", First_Fragment);
        if(mIsTwoPane)
            if(Second_Fragment!=null)
                getSupportFragmentManager().putFragment(outState, "Second_Fragment", Second_Fragment);
    }

    @Override
    public void setselectedmovie(Movie SelectedMovie) {
        // Case One Pane
        //Start Grid Details Main Activity
        if (!mIsTwoPane) {
            Intent i = new Intent(this, Details_Main.class);
            i.putExtra("object",SelectedMovie);
            i.putExtra("type",false);
            startActivity(i);
        } else {
            //Case Two-PAne
            //start Detail_Fragment
            Second_Fragment= new Detail_Fragment();
            Bundle extras= new Bundle();
            extras.putSerializable("object",SelectedMovie);
            Second_Fragment.setGrid_instance(First_Fragment); //adding instance of Grid_Fragment , to let run some function in two pane ui
            extras.putBoolean("type",true);
            Second_Fragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().replace(R.id.Detail_Main,Second_Fragment,"Second_Fragment").commit();
        }
    }
}
