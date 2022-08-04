package com.abdoulayeln.byblos;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.List;

public class RatingList extends ArrayAdapter<Rating> {
    private Activity context;
    List<Rating> ratings;

    public RatingList(Activity context, List<Rating> ratings) {
        super(context,R.layout.layout_rating_list, ratings);
        this.context = context;
        this.ratings = ratings;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_rating_list, null, true);

        TextView name = (TextView) listViewItem.findViewById(R.id.personName);
        TextView branch = (TextView) listViewItem.findViewById(R.id.branchName);
        TextView rating = (TextView) listViewItem.findViewById(R.id.rating);
        TextView comment = (TextView) listViewItem.findViewById(R.id.comment);


        Rating rate = ratings.get(position);
        name.setText("Customer: "+rate.getUserName());


        branch.setText("Branch: "+rate.getBranchName());
        rating.setText("Rating: "+rate.getRating());
        comment.setText("Comments: "+rate.getComments());


        return listViewItem;
    }
}