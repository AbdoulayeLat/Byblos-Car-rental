package com.abdoulayeln.byblos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class UserActivity extends AppCompatActivity {

    public FirebaseUser user;

    public DatabaseReference reference, branchRef;

    public String userID;
    private List branchs;
    EditText searchBox;
    ImageView searchImage;
    ListView branchView;
    boolean addBranch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User");
        branchRef = FirebaseDatabase.getInstance().getReference("Branch");
        userID = user.getUid();
        searchBox = (EditText) findViewById(R.id.etSearch);
        searchImage = (ImageView) findViewById(R.id.imSearch);
        branchView = (ListView) findViewById(R.id.lvBranch);
        branchs = new ArrayList<>();
        TextView createRequest = (TextView) findViewById(R.id.tvCreateRequest);
        TextView rateBranches = (TextView) findViewById(R.id.tvRateBranches);

        TextView welcomeUser = (TextView) findViewById(R.id.WelcomeUser);
        TextView role = (TextView) findViewById(R.id.LoggedInAs);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String name = userProfile.getFirstName();
                    String type = userProfile.getUserType();

                    String query = "Welcome, "+name+"!";
                    String query2 = "You are logged in as: "+type+".";
                    welcomeUser.setText(query);
                    role.setText(query2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserActivity.this, "Something went wrong!",Toast.LENGTH_LONG).show();
            }
        });

        // Show branch list before search
        branchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                branchs.clear();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Branch branch = dataSnapshot.getValue(Branch.class);
                    if(branch != null)
                        branchs.add(branch);
                }
                BranchList branchAdapter = new BranchList(UserActivity.this, branchs);
                branchView.setAdapter(branchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Show branch list only using searched branch
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                branchs.clear();
                searchMethod("branchName");
                searchMethod("startTime");
                searchMethod("endTime");
                searchMethodService();
                //Getting branch address
                branchRef.orderByChild("branchAddress").startAt(searchBox.getText().toString().trim())
                        .endAt(searchBox.getText().toString().trim()+"\uf8ff")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //branchs.clear();
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                    Branch branch = dataSnapshot.getValue(Branch.class);
                                    if(branch != null)
                                        branchs.add(branch);
                                }
                                BranchList branchAdapter = new BranchList(UserActivity.this, branchs);
                                branchView.setAdapter(branchAdapter);
                            }
                            @Override public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

        ImageView sign_out = (ImageView) findViewById(R.id.out);
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });

        createRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, CreateRequest.class);
                startActivity(intent);
            }
        });

        rateBranches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ratings = new Intent(UserActivity.this,RateBranches.class);
                startActivity(ratings);
            }
        });
    }

    private void searchMethodService() {

        DatabaseReference branchReference = FirebaseDatabase.getInstance().getReference("Branch");
        DatabaseReference serviceReference = FirebaseDatabase.getInstance().getReference("Service");

        branchReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Branch branch = dataSnapshot.getValue(Branch.class);
                    addBranch = false;
                    if(branch.getServicesOffered() == null)
                        return;
                    for(String serviceID: branch.getServicesOffered()){
                        DatabaseReference serviceReferenceBranch = serviceReference.child(serviceID);
                        serviceReferenceBranch.addListenerForSingleValueEvent(new ValueEventListener(){
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                Service service =  snapshot2.getValue(Service.class);
                                 if(service.getName().toLowerCase().equals(searchBox.getText().toString().trim().toLowerCase())){
                                    if(branch != null && !branchs.contains(branch)){
                                        branchs.add(branch);

                                    }
                                }
                               BranchList branchAdapter = new BranchList(UserActivity.this, branchs);
                               branchView.setAdapter(branchAdapter);
                            }

                            @Override public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void searchMethod(String query){
        branchRef.orderByChild(query).startAt(searchBox.getText().toString().trim())
                .endAt(searchBox.getText().toString().trim()+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //branchs.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Branch branch = dataSnapshot.getValue(Branch.class);
                    if(branch != null)
                        branchs.add(branch);
                }
                BranchList branchAdapter = new BranchList(UserActivity.this, branchs);
                branchView.setAdapter(branchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void makeToast(String str){ Toast.makeText(this, str, Toast.LENGTH_LONG).show();}
}
