package com.abdoulayeln.byblos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

public class EmployeeActivity extends AppCompatActivity {

    public FirebaseUser user;
    public DatabaseReference reference;
    public DatabaseReference branchReference, requestReference;
    public String userID;
    public TextView createBranch, branchInfo, requestList, currentState;
    public List<Branch> branchs;
    public List<Request> requests;
    public ListView listView, listView2;
    public boolean requestPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee);

        ImageView sign_out = (ImageView) findViewById(R.id.out);
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });





        requestPage = false;

        branchReference = FirebaseDatabase.getInstance().getReference("Branch");
        requestReference = FirebaseDatabase.getInstance().getReference("Request");
        reference = FirebaseDatabase.getInstance().getReference("User");

        branchInfo = findViewById(R.id.tvViewBranchList);
        requestList = findViewById(R.id.tvRequests);
        currentState = findViewById(R.id.currentState);
        createBranch = findViewById(R.id.tvCreateBranch);
        listView = findViewById(R.id.branchListView);
        listView2 = findViewById(R.id.branchListView2);

        branchs = new ArrayList<>();
        requests = new ArrayList<>();

        listView.setVisibility(View.VISIBLE);
        listView2.setVisibility(View.INVISIBLE);

        TextView welcomeEmployee = (TextView) findViewById(R.id.WelcomeEmployee);
        TextView role = (TextView) findViewById(R.id.LoggedInAs);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String name = userProfile.getFirstName();
                    String type = userProfile.getUserType();

                    String query = "Welcome, "+name+"!";
                    String query2 = "You are logged in as: "+type+".";
                    welcomeEmployee.setText(query);
                    role.setText(query2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EmployeeActivity.this, "Something went wrong!",Toast.LENGTH_LONG).show();
            }
        });

        requestList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentState.setText("Requests");
                requestPage = true;
                listView.setVisibility(View.INVISIBLE);
                listView2.setVisibility(View.VISIBLE);
                createBranch.setVisibility(View.INVISIBLE);

                requestReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        requests.clear();

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            Request req = dataSnapshot.getValue(Request.class);
                            if(req != null)
                                requests.add(req);
                        }
                        RequestList requestAdapter = new RequestList(EmployeeActivity.this, requests);
                        listView2.setAdapter(requestAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        branchInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentState.setText("Branch Info");
                createBranch.setVisibility(View.VISIBLE);
                requestPage = false;
                listView.setVisibility(View.VISIBLE);
                listView2.setVisibility(View.INVISIBLE);

                branchReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        branchs.clear();

                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            Branch branch = dataSnapshot.getValue(Branch.class);
                            if(branch != null)
                                branchs.add(branch);
                        }
                        BranchList branchAdapter = new BranchList(EmployeeActivity.this, branchs);
                        listView.setAdapter(branchAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });



        branchReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                branchs.clear();

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Branch branch = dataSnapshot.getValue(Branch.class);
                    if(branch != null)
                        branchs.add(branch);
                }
                BranchList branchAdapter = new BranchList(EmployeeActivity.this, branchs);
                listView.setAdapter(branchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(EmployeeActivity.this, EditBranch.class);
                Branch branch = branchs.get(i);
                intent.putExtra("EDITED_BRANCH", branch);
                startActivity(intent);
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(EmployeeActivity.this, requestsEmployeeSide.class);
                intent.putExtra("REQUEST_OBJECT2", requests.get(i));
                startActivity(intent);
                //Request requestS = requests.get(i);
                //showChangeStatusDialog(requestS);
            }
        });


        createBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(requestPage == false){
                    Intent intent = new Intent(EmployeeActivity.this, CreateBranch.class);
                    startActivity(intent);
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //Method to show service creation window
    private void showChangeStatusDialog(Request request){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.change_status, null);
        dialogBuilder.setView(dialogView);

        final Button btnPending = (Button) dialogView.findViewById(R.id.btnPending);
        final Button btnDenied = (Button) dialogView.findViewById(R.id.btnDenied);
        final Button btnApproved = (Button) dialogView.findViewById(R.id.btnApproved);
        final Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        TextView etRequestID = dialogView.findViewById(R.id.etRequestID);
        TextView etRequestStatus = dialogView.findViewById(R.id.etRequestStatus);

        etRequestID.setText(request.getRefId());
        etRequestStatus.setText(request.getStatus());

        final AlertDialog b = dialogBuilder.create();
        b.show();

        btnApproved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request.setStatus("approved");
                DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Request").child(request.getRefId());
                dr.setValue(request);

                makeToast("Status updated");
                b.dismiss();
            }
        });

        btnDenied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request.setStatus("denied");
                DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Request").child(request.getRefId());
                dr.setValue(request);

                makeToast("Status updated");
                b.dismiss();
            }
        });

        btnPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                request.setStatus("pending");
                DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Request").child(request.getRefId());
                dr.setValue(request);

                makeToast("Status updated");
                b.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });
    }

    private void makeToast(String str){
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }


}