package com.abdoulayeln.byblos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class requestsEmployeeSide extends AppCompatActivity {

    public Button btnCancel, updateStatus;
    public TextView reqId, userId, requestStatus, listView;
    List<String> requirements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_requests_employee_side);

        Request request = (Request) getIntent().getSerializableExtra("REQUEST_OBJECT2");



        List<String> idToNames = new ArrayList<>();

        btnCancel = (Button) findViewById(R.id.btnCancel);
        updateStatus= (Button) findViewById(R.id.btnUpdateStatus);
        requestStatus  = findViewById(R.id.etRequestStatus);
        reqId = findViewById(R.id.etRequestID);
        userId = findViewById(R.id.etUserID);
        listView = findViewById(R.id.listView);

        userId.setText(request.getUserID());
        reqId.setText(request.getRefId());
        requestStatus.setText(request.getStatus());



        DatabaseReference branchReference = FirebaseDatabase.getInstance().getReference("Branch").child(request.getBranchId());
        TextView branchName = findViewById(R.id.etBranchName);

        branchReference.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Branch branch =  snapshot.getValue(Branch.class);
                branchName.setText(branch.getBranchName());
            }

            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });

        DatabaseReference serviceReference = FirebaseDatabase.getInstance().getReference("Service").child(request.getServiceId());
        TextView serviceName = findViewById(R.id.etServiceRequested);

        serviceReference.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Service branch =  snapshot.getValue(Service.class);
                serviceName.setText(branch.getName());
                String reqData = "Required data: ";
                for (String reqItem: branch.getRequirementList()){
                    reqData += reqItem + ", ";
                }
                reqData += "\n-------------------------\n\n\n";

                List<String> requirements = request.getRequirementsFulfilled().keySet().stream().collect(Collectors.toList());
                HashMap<String,String>  req = request.getRequirementsFulfilled();

                for (String reqItem: requirements){
                    reqData += reqItem +": \n" + req.get(reqItem) +"\n\n";
                }
                listView.setText(reqData);
            }

            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeStatusDialog(request);
                requestStatus.setText(request.getStatus());
            }
        });
    }

    private void fillBranch(Request request){
        DatabaseReference branchReference = FirebaseDatabase.getInstance().getReference("Branch").child(request.getBranchId());
        TextView branchName = findViewById(R.id.etBranchName);

        branchReference.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Branch branch =  snapshot.getValue(Branch.class);
                branchName.setText(branch.getBranchName());
            }

            @Override public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

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

    private void makeToast(String str){ Toast.makeText(this, str, Toast.LENGTH_LONG).show();}
}