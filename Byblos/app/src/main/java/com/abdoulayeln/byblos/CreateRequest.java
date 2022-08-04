package com.abdoulayeln.byblos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

public class CreateRequest extends AppCompatActivity {

    public Button btnCreate, btnCancel;
    public TextView branchName, serviceName, branchServicesList;

    public ListView listView,listView2;

    public FirebaseUser user;
    public DatabaseReference reference;
    public DatabaseReference branchReference;
    public String userID;
    String branchID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_request);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User");
        userID = user.getUid();

        Request request = new Request();
        request.setUserID(userID);

        listView = findViewById(R.id.listView);
        listView2 = findViewById(R.id.listView2);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCreate = (Button) findViewById(R.id.btnCreateBranch);
        branchName = findViewById(R.id.etBranchName);
        serviceName = findViewById(R.id.etServiceName);
        //branchServicesList = findViewById(R.id.branchServicesList);

        DatabaseReference branchReference = FirebaseDatabase.getInstance().getReference("Branch");
        DatabaseReference serviceReference = FirebaseDatabase.getInstance().getReference("Service");

        List<Branch> branchesAvailable = new ArrayList<>();
        List<Service> servicesOffered = new ArrayList<>();

        branchReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Branch branch= dataSnapshot.getValue(Branch.class);
                    if(branch != null)
                        branchesAvailable.add(branch);
                }

                BranchList branchAdapter = new BranchList(CreateRequest.this, branchesAvailable, true);
                listView.setAdapter(branchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Branch branch =  branchesAvailable.get(i);
                branchID = branch.getBranchID();
                request.setBranchId(branchID);
                branchName.setText(branch.getBranchName());
                List<String> servicesOfferedID = branch.getServicesOffered();

                serviceReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        servicesOffered.clear();
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            Service service= dataSnapshot.getValue(Service.class);
                            if(service != null){
                                for(String offeredService: servicesOfferedID){
                                    if(offeredService.equals(service.getId()))
                                        servicesOffered.add(service);
                                }
                            }

                        }
                        ServiceList serviceAdapter = new ServiceList(CreateRequest.this, servicesOffered);
                        listView2.setAdapter(serviceAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Service service = servicesOffered.get(i);
                serviceName.setText(service.getName());
                request.setServiceId(service.getId());
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if(branchName.getText().equals("") || serviceName.getText().equals("")){
                    Toast.makeText(getApplicationContext(), "Select Branch and Service ", Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent(CreateRequest.this, RequestRequirements.class);
                    intent.putExtra("REQUEST_OBJECT", request);
                    startActivity(intent);
                    finish();
                }
            }
        });



    }
}
