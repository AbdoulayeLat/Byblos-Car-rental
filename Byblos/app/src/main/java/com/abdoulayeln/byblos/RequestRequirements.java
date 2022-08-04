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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestRequirements extends AppCompatActivity {

    public Button btnCreate, btnCancel;
    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_request_requirements);

        Request request = (Request) getIntent().getSerializableExtra("REQUEST_OBJECT");

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        listView = findViewById(R.id.listView);


        DatabaseReference serviceReference = FirebaseDatabase.getInstance().getReference("Service").child(request.getServiceId());
        List<String> requirements= new ArrayList<String>();
        serviceReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Service serviceSelected = snapshot.getValue(Service.class);
                for( String req : serviceSelected.getRequirementList()){
                    requirements.add(req);
                }
                RequirementsList requirementAdapter = new RequirementsList(RequestRequirements.this, serviceSelected.getRequirementList());
                listView.setAdapter(requirementAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String req = requirements.get(i);
                String dat = request.getRequirementData(req);
                if(dat == null)
                    dat = "";
                showProvideDataDialog(req, dat);

                dat = request.getRequirementData(req);
                if(dat != null){
                    TextView etData = view.findViewById(R.id.etData);
                    etData.setText(dat);
                    etData.invalidate();
                }
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
                DatabaseReference requestReference = FirebaseDatabase.getInstance().getReference("Request");
                String requestID = requestReference.push().getKey();
                request.setRefId(requestID);
                requestReference.child(requestID).setValue(request);
                Toast.makeText(getApplicationContext(), "Request Created", Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }

    private void showProvideDataDialog(String request, String data){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_requirement_request, null);
        dialogBuilder.setView(dialogView);

        final Button btnSubmit = (Button) dialogView.findViewById(R.id.btnSubmit);
        final Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel2);
        TextView etRequirementName = dialogView.findViewById(R.id.etRequirementName);
        EditText etProvidedData = (EditText) dialogView.findViewById(R.id.etProvidedData);

        etRequirementName.setText(request);
        etProvidedData.setText(data);

        final AlertDialog b = dialogBuilder.create();
        b.show();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Request request = (Request) getIntent().getSerializableExtra("REQUEST_OBJECT");
                request.addRequirementData(etRequirementName.getText().toString().trim(), etProvidedData.getText().toString().trim());
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
}