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

public class BranchList extends ArrayAdapter<Branch> {
    private Activity context;
    List<Branch> branches;
    boolean shortView;

    public BranchList(Activity context, List<Branch> branches) {
        super(context, R.layout.layout_branch_list, branches);
        this.context = context;
        this.branches = branches;
        this.shortView =false;
    }

    public BranchList(Activity context, List<Branch> branches, boolean shortView) {
        super(context, R.layout.layout_branch_list, branches);
        this.context = context;
        this.branches = branches;
        this.shortView =shortView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_branch_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewBranchName);
        TextView textViewAddress = (TextView) listViewItem.findViewById(R.id.textViewBranchAddress);
        TextView textViewPhoneNumber = (TextView) listViewItem.findViewById(R.id.textViewBranchPhoneNumber);
        TextView textViewStartTime = (TextView) listViewItem.findViewById(R.id.textViewBranchStartTime);
        TextView textViewEndTime = (TextView) listViewItem.findViewById(R.id.textViewBranchEndTime);


        Branch branch = branches.get(position);
        textViewName.setText("Branch Name: "+branch.getBranchName());

        if(shortView){
            textViewEndTime.setVisibility(View.INVISIBLE);
            textViewStartTime.setVisibility(View.INVISIBLE);
            textViewPhoneNumber.setVisibility(View.INVISIBLE);
            textViewEndTime.setHeight(0);
            textViewStartTime.setHeight(0);
            textViewPhoneNumber.setHeight(0);
        }else{
            textViewPhoneNumber.setText("Branch Phone Number: "+branch.getBranchPhoneNumber());
            textViewStartTime.setText("Branch working hours start time: "+branch.getStartTime());
            textViewEndTime.setText("Branch working hours end time: "+branch.getEndTime());
            textViewAddress.setText("Branch Address: "+branch.getBranchAddress());
        }

        return listViewItem;
    }
}
