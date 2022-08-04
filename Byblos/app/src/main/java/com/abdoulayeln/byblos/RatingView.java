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

public class RatingView extends AppCompatActivity {

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
                ratingSet.clear();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    //makeToast(String.valueOf(dataSnapshot.getValue() == null));
                    Rating rate = dataSnapshot.getValue(Rating.class);

                    if(rate != null)
                        ratingSet.add(rate);
                }
                RatingList ratingAdapter = new RatingList(RatingView.this, ratingSet);
                ratings.setAdapter(ratingAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RatingView.this, RateBranches.class);
                startActivity(intent);
            }
        });

    }

    private void makeToast(String str){
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }
}
