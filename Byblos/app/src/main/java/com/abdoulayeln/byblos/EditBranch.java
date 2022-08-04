package com.abdoulayeln.byblos;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditBranch extends AppCompatActivity {
    public Button btnUpdate, btnCancel, btnDelete;
    public EditText branchName, branchAddress, branchPhoneNumber;
    public TimePicker branchStartTime, branchEndTime;
    public ListView listView,listView2;

    List<Service> servicesList;

    public FirebaseUser user;
    public DatabaseReference reference;
    public DatabaseReference branchReference;
    public String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_branch);

        Branch branch = (Branch) getIntent().getSerializableExtra("EDITED_BRANCH");

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User");
        userID = user.getUid();

        listView = findViewById(R.id.listView);
        listView2 = findViewById(R.id.listView2);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnUpdate = (Button) findViewById(R.id.btnUpdateBranch);
        btnDelete = (Button) findViewById(R.id.btnDeleteBranch);
        branchName = (EditText) findViewById(R.id.etBranchName);
        branchAddress = (EditText) findViewById(R.id.etBranchAddress);
        branchPhoneNumber = (EditText) findViewById(R.id.etBranchPhoneNumber);
        branchStartTime = (TimePicker) findViewById(R.id.startTime);
        branchStartTime.setIs24HourView(true);
        branchEndTime = (TimePicker) findViewById(R.id.endTime);
        branchEndTime.setIs24HourView(true);

        branchName.setText(branch.getBranchName());
        branchAddress.setText(branch.getBranchAddress());
        branchPhoneNumber.setText(branch.getBranchPhoneNumber());
        branchStartTime.setHour(Integer.valueOf(branch.getStartTime().split(":")[0]));
        branchStartTime.setMinute(Integer.valueOf(branch.getStartTime().split(":")[1]));
        branchEndTime.setHour(Integer.valueOf(branch.getEndTime().split(":")[0]));
        branchEndTime.setMinute(Integer.valueOf(branch.getEndTime().split(":")[1]));

        DatabaseReference serviceReference = FirebaseDatabase.getInstance().getReference("Service");
        List<Service> servicesAvailable = new ArrayList<>();
         servicesList = new ArrayList<>();
        List<Service> servicesOffered = new ArrayList<>();
        String str="";
        serviceReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                servicesAvailable.clear();
                List<String> servicesOfferedID = branch.getServicesOffered();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Service service= dataSnapshot.getValue(Service.class);
                        servicesAvailable.add(service);

                        for (String servId : servicesOfferedID){
                            if(service.getId().equals(servId)){
                                servicesOffered.add(service);
                                break;
                            }
                        }
                }
                ServiceList serviceAdapter = new ServiceList(EditBranch.this, servicesAvailable);
                listView.setAdapter(serviceAdapter);
                ServiceList serviceAdapter2 = new ServiceList(EditBranch.this, servicesOffered);
                listView2.setAdapter(serviceAdapter2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Service service = servicesAvailable.get(i);
                if( !servicesOffered.contains(service)){
                    servicesOffered.add(service);
                    ServiceList serviceAdapter2 = new ServiceList(EditBranch.this, servicesOffered);
                    listView2.setAdapter(serviceAdapter2);
                }
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Service service = servicesOffered.get(i);
                servicesOffered.remove(i);
                ServiceList serviceAdapter2 = new ServiceList(EditBranch.this, servicesOffered);
                listView2.setAdapter(serviceAdapter2);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = branchName.getText().toString().trim();
                String phoneNumber = branchPhoneNumber.getText().toString().trim();
                String address = branchAddress.getText().toString().trim();
                int startTimeHour = branchStartTime.getHour();
                int startTimeMinute = branchStartTime.getMinute();
                int endTimeHour = branchEndTime.getHour();
                int endTimeMinute = branchEndTime.getMinute();
                String startTime = startTimeHour+":"+startTimeMinute;
                String endTime = endTimeHour+":"+endTimeMinute;
                List<String> servicesOfferedIDnew = new ArrayList<>();
                for(Service service : servicesOffered){
                    servicesOfferedIDnew.add(service.getId());
                }

                // Name must only contain letters (upper or lower case) or spaces
                boolean isNameValid = !name.isEmpty() && name.matches("^[A-Z|a-z| ]+$");

                // Correct format: [int] [string], [string], [string], [6 int or char], [string]
                // [House / Unit number] [Stress Name / Address], [City], [State / Province / Region], [ZIP / Postal Code], [Country]
                boolean isAddressValid = !address.isEmpty() && address.matches("^(\\d+)(\\s[a-z|A-Z|\\s]+,){3}\\s([a-z|A-Z|0-9]){6},\\s[a-z|A-Z|\\s]+$");

                // Note: phone number must be 9-12 digits (no spaces or dashes allowed)
                boolean isPhoneValid = !phoneNumber.isEmpty() && phoneNumber.matches("^[0-9]{9,12}$");

                // the start time hour must be earlier (smaller) than end time hour
                boolean isTimeValid = (startTimeHour < endTimeHour);

                if (!isNameValid) {
                    Toast.makeText(getApplicationContext(), "Name is invalid - must only contain letters (upper or lower case) or spaces", Toast.LENGTH_LONG).show();
                } else if (!isAddressValid){
                    Toast.makeText(getApplicationContext(), "Address is invalid - Correct format: [int] [string], [string], [string], [6 int or char], [string]", Toast.LENGTH_LONG).show();
                } else if (!isPhoneValid) {
                    Toast.makeText(getApplicationContext(), "Phone number is invalid - must be 9-12 digits (no spaces or dashes allowed)", Toast.LENGTH_LONG).show();
                } else if (!isTimeValid) {
                    Toast.makeText(getApplicationContext(), "Start time must be earlier than end time", Toast.LENGTH_LONG).show();
                } else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Branch").child(branch.getBranchID());
                    Branch branch2 = new Branch(branch.getBranchID(), name, phoneNumber, address, startTime, endTime, servicesOfferedIDnew);
                    databaseReference.setValue(branch2);
                    Toast.makeText(getApplicationContext(), "Branch Updated", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Branch").child(branch.getBranchID());
                databaseReference.removeValue();
                Toast.makeText(getApplicationContext(), "Branch Deleted", Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }
}
