package com.abdoulayeln.byblos;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RequirementsList extends ArrayAdapter<String> {
    private Activity context;
    ArrayList<String> requirements;

    public RequirementsList(Activity context, ArrayList<String> requirements) {
        super(context, R.layout.request_requirement_list, requirements);
        this.context = context;
        this.requirements = requirements;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.request_requirement_list, null, true);

        TextView requirementTextview = (TextView) listViewItem.findViewById(R.id.etRequirement);
        TextView dataTextview = (TextView) listViewItem.findViewById(R.id.etData);
        requirementTextview.setText(requirements.get(position));

        return listViewItem;
    }
}
