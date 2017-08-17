package com.matrawy.a7oda.Final_Project;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.matrawy.a7oda.stageone.R;

import java.util.List;
public class Trailer_Adapter extends ArrayAdapter<String> {
    Context m;
    public Trailer_Adapter(Activity context, List<String> Text){

        super(context,0,Text);
        m=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String Text = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailertheme,parent,false);
        }

        if (Text != null) {
            TextView m = (TextView) convertView.findViewById(R.id.Trailer_id);
           if(m!=null) {
               m.setText(Text.toString());
               ImageView iconView = (ImageView) convertView.findViewById(R.id.image);
               iconView.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }
        }
        return  convertView;
    }

}
