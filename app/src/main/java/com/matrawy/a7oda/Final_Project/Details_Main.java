package com.matrawy.a7oda.Final_Project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.matrawy.a7oda.stageone.R;

public class Details_Main extends AppCompatActivity {
    Detail_Fragment detail_fragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_main);

        Movie current_move = (Movie) getIntent().getSerializableExtra("object");

        detail_fragment = new Detail_Fragment();
        Bundle packge = new Bundle();
        packge.putSerializable("object",current_move);
        packge.putBoolean("type",(boolean) getIntent().getBooleanExtra("type",false));
        detail_fragment.setArguments(packge);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            detail_fragment = (Detail_Fragment) getSupportFragmentManager().getFragment(savedInstanceState, "detail_Fragment");
        }
        else{
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.Detail_Main, detail_fragment,"detail_Fragment")
                    .commit();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "detail_Fragment", detail_fragment);
    }
}