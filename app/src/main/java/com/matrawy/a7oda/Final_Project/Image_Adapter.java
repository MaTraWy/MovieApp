package com.matrawy.a7oda.Final_Project;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.matrawy.a7oda.stageone.R;

import java.util.List;

import it.sephiroth.android.library.picasso.Picasso;


public class Image_Adapter extends ArrayAdapter<Movie> {
    Context m;
    public Image_Adapter(Activity context, List<Movie> movieList){

        super(context,0,movieList);
        m=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie current_movie = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.img, parent, false);
        }
        ImageView img = (ImageView) convertView.findViewById(R.id.img);
        Picasso.with(m).load(current_movie.getImg_main()).into(img);
        img.setAdjustViewBounds(true);
        return convertView;
    }
}
