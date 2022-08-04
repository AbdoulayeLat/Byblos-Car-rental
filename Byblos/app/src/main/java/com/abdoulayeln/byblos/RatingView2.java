package com.abdoulayeln.byblos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RatingView2 extends AppCompatActivity {

    Button back;
    ListView ratings;
    DatabaseReference reference;
    List<Rating> ratingSet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ratings_list);

        back = (Button) findViewById(R.id.back);
        ratings = (ListView) findViewById(R.id.comments);
        reference = FirebaseDatabase.getInstance().getReference("Rating");
        ratingSet = new ArrayList<Rating>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    //Log.i("tag","testLogCat");
                    Rating rate = dataSnapshot.getValue(Rating.class);

                    if(rate != null){
                        ratingSet.add(rate);
                    }
                }
                RatingList ratingAdapter = new RatingList(RatingView2.this, ratingSet);
                ratings.setAdapter(ratingAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
