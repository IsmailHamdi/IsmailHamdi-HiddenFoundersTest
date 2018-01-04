package com.example.ismail.hiddenfounderstest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ismail on 02.01.2018.
 * ListView Adapter to personalise the view to the one in the xml file  (list_view_item.xml)
 */

public class Adapter extends BaseAdapter {

    private LayoutInflater Inflater;
    private List<Repository> DataSource; // the list that contain data witch be shoen in the listview

    public Adapter(android.content.Context context, List<Repository> dataSource) {
        this.DataSource = dataSource;
        this.Inflater =  (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE); //retrieve a standard LayoutInflater instance that is already hooked up to the current context
    }
    public  Adapter(Context context){
        this.DataSource = new ArrayList<Repository>();
        this.Inflater =  (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE); //retrieve a standard LayoutInflater instance that is already hooked up to the current context
    }

    /**
     * A method that change the data source of the list view instantly
     * @param dataSource : the new data source
     */
    public void changeDataSource(List<Repository>dataSource){
        this.DataSource = dataSource;

        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return this.DataSource.size();
    }

    @Override
    public Object getItem(int i) {
        return this.DataSource.get(i);
    }

    @Override
    public long getItemId(int i) {
        return this.DataSource.get(i).getId();
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View row = Inflater.inflate(R.layout.list_view_item, viewGroup, false);//inflate the view of the row from xml file

        TextView name,ownerName,numberOfStar;
        final TextView description;
        ImageView ownerAvatar;

        name = (TextView) row.findViewById(R.id.RepoNameTextView);
        description = (TextView) row.findViewById(R.id.RepoDescriptionTextView);
        ownerName = (TextView) row.findViewById(R.id.RepoOwnerNameTextView);
        numberOfStar = (TextView) row.findViewById(R.id.RepoNumberStarsTextView);
        ownerAvatar = (ImageView) row.findViewById(R.id.RepoOwnerAvatar);

        name.setText(DataSource.get(i).getName().replace("\n"," ")); //replacing the \n with a blank space to avoid multi line in Repository and owner name

        description.setText(DataSource.get(i).getDescription().replace("\n"," "));

        final Adapter tmp = this;

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {  // if the description textview clicked, it check if he is already shrinked or not
                if(description.getMaxLines() > 1){  // if not it will set the SingleLine Proprety to true to shrink it.
                    description.setSingleLine(true);
                }else {
                    description.setSingleLine(false); // else it will develop it
                }
            }
        });

        ownerName.setText(DataSource.get(i).getOwnerName().replace("\n"," "));
        ownerAvatar.setImageBitmap(DataSource.get(i).getOwnerAvatar());
        if(DataSource.get(i).getNumberStars()>=0.1) {
            numberOfStar.setText(DataSource.get(i).getNumberStars().toString() + " K");
        }
        else {

            numberOfStar.setText(Math.round(DataSource.get(i).getNumberStars()*1000) + "   ");
        }
        return row;
    }
}
