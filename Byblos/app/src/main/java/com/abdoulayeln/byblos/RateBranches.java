package com.abdoulayeln.byblos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RateBranches extends AppCompatActivity{

    public FirebaseUser user;

    public DatabaseReference reference, branchRef;

    public String userID;
    Button submit, otherRatings,back;
    EditText comments,userName;
    Spinner rating;
    Spinner branch;
    List branches;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_branches);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User");
        branchRef = FirebaseDatabase.getInstance().getReference("Branch");
        userID = user.getUid();

        submit = (Button) findViewById(R.id.submit);
        otherRatings = (Button) findViewById(R.id.otherRatings);
        back = (Button) findViewById(R.id.back);
        branch = (Spinner) findViewById(R.id.branchName);
        comments = (EditText) findViewById(R.id.comments);
        rating = (Spinner) findViewById(R.id.rating);
        branches = new ArrayList<>();
        userName = (EditText) findViewById(R.id.userName);


        branchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                branches.clear();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Branch branch = dataSnapshot.getValue(Branch.class);
                    if(branch != null)
                        branches.add(branch.getBranchName());
                }
                ArrayAdapter<String> branchAdapter = new ArrayAdapter<String>(RateBranches.this, android.R.layout.simple_spinner_dropdown_item, branches);
                branch.setAdapter(branchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String[] stars = new String[]{"1","2","3","4","5"};
        ArrayAdapter<String> ratingAdapter = new ArrayAdapter<String>(RateBranches.this, android.R.layout.simple_spinner_dropdown_item, stars);
        rating.setAdapter(ratingAdapter);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = userName.getText().toString().trim();
                String branchName = branch.getSelectedItem().toString();
                String comment = comments.getText().toString().trim();
                String userRating = rating.getSelectedItem().toString();

                DatabaseReference db = FirebaseDatabase.getInstance().getReference("Rating");
                String ratingID = db.push().getKey();
                Rating r = new Rating(ratingID,name,branchName,comment, userRating);
                db.child(ratingID).setValue(r);
                makeToast("Rating successfully made");
            }
        });

        otherRatings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RateBranches.this, RatingView2.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void makeToast(String str){
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }
}
