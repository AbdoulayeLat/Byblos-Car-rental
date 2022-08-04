package com.abdoulayeln.byblos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AdministratorActivity extends AppCompatActivity {
    private TextView loadUser, loadService;
    private Button createService, createUser;
    private DatabaseReference serviceDatabaseReference; //Reference for Service List showing
    private DatabaseReference userDatabaseReference;
    ListView listView;
    ListView listView2;
    List<Service> services;
    List<User> users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);

        ImageView sign_out = (ImageView) findViewById(R.id.out);
        sign_out.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FirebaseAuth.getInstance().signOut();
               finish();
           }
        });

        serviceDatabaseReference = FirebaseDatabase.getInstance().getReference("Service");
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("User");
        createService = (Button) findViewById(R.id.btnCreateService);
        loadUser = (TextView) findViewById(R.id.tvLoadUsers);
        loadService = (TextView) findViewById(R.id.tvLoadServices);
        listView = (ListView) findViewById(R.id.listViewAdmin);
        listView2 = (ListView) findViewById(R.id.listViewAdmin2);
        services = new ArrayList<>();
        users = new ArrayList<>();
        createUser = (Button) findViewById(R.id.btnCreateUser);

        createUser.setVisibility(View.INVISIBLE);
        createService.setVisibility(View.VISIBLE);

        listView.setVisibility(View.VISIBLE);
        listView2.setVisibility(View.INVISIBLE);

        loadUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser.setVisibility(View.VISIBLE);
                createService.setVisibility(View.INVISIBLE);

                listView.setVisibility(View.INVISIBLE);
                listView2.setVisibility(View.VISIBLE);

                userDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        users.clear();
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            User user = dataSnapshot.getValue(User.class);
                            if(user != null)
                                users.add(user);
                        }
                        UserList userAdapter = new UserList(AdministratorActivity.this, users);
                        listView2.setAdapter(userAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        loadService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser.setVisibility(View.INVISIBLE);
                createService.setVisibility(View.VISIBLE);

                listView.setVisibility(View.VISIBLE);
                listView2.setVisibility(View.INVISIBLE);

                serviceDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        services.clear();
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            Service service = dataSnapshot.getValue(Service.class);
                            if(service != null)
                                services.add(service);
                        }
                        ServiceList serviceAdapter = new ServiceList(AdministratorActivity.this, services);
                        listView.setAdapter(serviceAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        serviceDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                services.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Service service = dataSnapshot.getValue(Service.class);
                    if(service != null)
                        services.add(service);
                }
                ServiceList serviceAdapter = new ServiceList(AdministratorActivity.this, services);
                listView.setAdapter(serviceAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        createService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateServiceDialog();
            }
        });

        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateUserDialog();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Service service = services.get(i);
                showUpdateAndDeleteDialog(service.getId(),service.getName(), service.getFee(), service);
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = users.get(i);
                showUpdateAndDeleteDialogUsers(user.getID(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getUserType());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void showUpdateAndDeleteDialogUsers(final String id, String firstName, String lastName, String email, String password, String type){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_delete_users,null);
        dialogBuilder.setView(dialogView);

        final TextView title = (TextView) dialogView.findViewById(R.id.title);
        title.setText("Edit User: "+firstName);
        final EditText editUserFirstName = (EditText) dialogView.findViewById(R.id.editUserFirstName);
        final EditText editUserLastName = (EditText) dialogView.findViewById(R.id.editUserLastName);
        final EditText editUserEmail = (EditText) dialogView.findViewById(R.id.editUserEmail);
        final EditText editUserPassword = (EditText) dialogView.findViewById(R.id.editUserpassword);
        final EditText editUserType = (EditText) dialogView.findViewById(R.id.editUserType);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateService);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteService);

        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstname = editUserFirstName.getText().toString().trim();
                String lastname = editUserLastName.getText().toString().trim();
                String email = editUserEmail.getText().toString().trim();
                String password = editUserPassword.getText().toString().trim();
                String type = editUserType.getText().toString().trim();

                // Name must only contain letters (upper or lower case) or spaces
                boolean isNameValid = !firstname.isEmpty() && !lastname.isEmpty() &&  firstname.matches("^[A-Z|a-z| ]+$") && lastname.matches("^[A-Z|a-z| ]+$");
                // Email must be in the format [string]@[string].[string]
                boolean isEmailValid = !email.isEmpty() && email.matches("^(\\w)+@(\\w)+\\.(\\w)+$");
                // password is only check for being none empty
                boolean isPasswordValid = !password.isEmpty();
                // userType must be admin, customer or employee
                boolean isUserValid = !type.isEmpty() && (type.equals("customer") || type.equals("Byblos Employee"));

                //if (!TextUtils.isEmpty(firstname) && !TextUtils.isEmpty(lastname) && !TextUtils.isEmpty(emailAddress) && !TextUtils.isEmpty(passWord) && !TextUtils.isEmpty(userType)){
                if (!isNameValid) {
                    Toast.makeText(getApplicationContext(), "Name is invalid - must only contain letters (upper or lower case) or spaces", Toast.LENGTH_LONG).show();
                } else if (!isEmailValid) {
                    Toast.makeText(getApplicationContext(), "Email is invalid - must be [string]@[string].[string]", Toast.LENGTH_LONG).show();
                } else if (!isPasswordValid) {
                    Toast.makeText(getApplicationContext(), "Password is invalid - must not be empty", Toast.LENGTH_LONG).show();
                } else if (!isUserValid) {
                    Toast.makeText(getApplicationContext(), "User is invalid - must 'customer' or 'Byblos Employee'", Toast.LENGTH_LONG).show();
                } else {
                    updateUser(id, firstname, lastname, email, password, type);
                    b.dismiss();
                }/*else{
                    fieldsEmptyMessage();
                }*/
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser(id);
                b.dismiss();
            }
        });
    }

    private void updateUser(final String id, String firstName, String lastName, String email, String password, String type){
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("User").child(id);

        User user = new User(id, firstName, lastName, email, password, type);
        dr.setValue(user);

        Toast.makeText(this, "User updated", Toast.LENGTH_LONG).show();
    }

    private void deleteUser(final String id){
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("User").child(id);

        dr.removeValue();
        Toast.makeText(this, "User deleted", Toast.LENGTH_LONG).show();
    }

    private void showCreateUserDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.create_user, null);
        dialogBuilder.setView(dialogView);

        final Button btnCreate = (Button) dialogView.findViewById(R.id.btnCreateBranch);
        final EditText firstName = (EditText) dialogView.findViewById(R.id.etBranchName);
        final EditText lastName = (EditText) dialogView.findViewById(R.id.etBranchAddress);
        final EditText email = (EditText) dialogView.findViewById(R.id.etBranchEmail);
        final EditText password = (EditText) dialogView.findViewById(R.id.etPassword);
        final EditText type = (EditText) dialogView.findViewById(R.id.etType);

        final AlertDialog b = dialogBuilder.create();
        b.show();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstname = firstName.getText().toString().trim();
                String lastname = lastName.getText().toString().trim();
                String emailAddress = email.getText().toString().trim();
                String passWord = password.getText().toString().trim();
                String userType = type.getText().toString().trim();

                // Name must only contain letters (upper or lower case) or spaces
                boolean isNameValid = !firstname.isEmpty() && !lastname.isEmpty() &&  firstname.matches("^[A-Z|a-z| ]+$") && lastname.matches("^[A-Z|a-z| ]+$");
                // Email must be in the format [string]@[string].[string]
                boolean isEmailValid = !emailAddress.isEmpty() && emailAddress.matches("^(\\w)+@(\\w)+\\.(\\w)+$");
                // password is only check for being none empty
                boolean isPasswordValid = !passWord.isEmpty();
                // userType must be admin, customer or employee
                boolean isUserValid = !userType.isEmpty() && (userType.equals("customer") || userType.equals("Byblos Employee"));

                //if (!TextUtils.isEmpty(firstname) && !TextUtils.isEmpty(lastname) && !TextUtils.isEmpty(emailAddress) && !TextUtils.isEmpty(passWord) && !TextUtils.isEmpty(userType)){
                if (!isNameValid) {
                    Toast.makeText(getApplicationContext(), "Name is invalid - must only contain letters (upper or lower case) or spaces", Toast.LENGTH_LONG).show();
                } else if (!isEmailValid) {
                    Toast.makeText(getApplicationContext(), "Email is invalid - must be [string]@[string].[string]", Toast.LENGTH_LONG).show();
                } else if (!isPasswordValid) {
                    Toast.makeText(getApplicationContext(), "Password is invalid - must not be empty", Toast.LENGTH_LONG).show();
                } else if (!isUserValid) {
                    Toast.makeText(getApplicationContext(), "User is invalid - must 'customer' or 'Byblos Employee'", Toast.LENGTH_LONG).show();
                } else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
                    String userID = databaseReference.push().getKey();
                    User user = new User(userID, firstname, lastname, emailAddress, passWord, userType);
                    databaseReference.child(userID).setValue(user);

                    Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_LONG).show();
                    b.dismiss();
                }/*else{
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
                }*/
            }
        });

    }

    private void showUpdateAndDeleteDialog(final String productID, String serviceName, double rate, Service service){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_delete_services,null);
        dialogBuilder.setView(dialogView);

        final TextView title = (TextView) dialogView.findViewById(R.id.title);
        title.setText("Edit Service: "+serviceName);
        EditText editServiceName = (EditText) dialogView.findViewById(R.id.editServiceName);
        EditText editServiceRate = (EditText) dialogView.findViewById(R.id.editServiceRate);
        EditText editServiceRequrements = (EditText) dialogView.findViewById(R.id.editServiceRequirements);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateService);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteService);

        editServiceRequrements.setText(service.getRequirement());
        editServiceName.setText(serviceName);
        editServiceRate.setText(Double.valueOf(rate).toString());

        final AlertDialog b = dialogBuilder.create();
        b.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editServiceName.getText().toString().trim();
                String req = editServiceRequrements.getText().toString().trim();
                String rateString = editServiceRate.getText().toString().trim();

                // Name must only contain letters (upper or lower case) or spaces
                boolean isNameValid = !name.isEmpty() && name.matches("^[A-Z|a-z| ]+$");
                // Requirements is only checked for being none empty (no need to add extra limitations)
                boolean isReqValid = !req.isEmpty();
                // Rate must be numeric (but have have a decimal number)
                boolean isRateValid = !rateString.isEmpty() && rateString.matches("^[0-9]+(\\.)?[0-9]*$");

                if (!isNameValid) {
                    Toast.makeText(getApplicationContext(), "Name invalid - must only contain letters (upper or lower case) or spaces", Toast.LENGTH_LONG).show();
                } else if (!isReqValid) {
                    Toast.makeText(getApplicationContext(), "Requirements invalid - must not be empty", Toast.LENGTH_LONG).show();
                } else if (!isRateValid) {
                    Toast.makeText(getApplicationContext(), "Rate invalid - must be numeric (but have have a decimal number)", Toast.LENGTH_LONG).show();
                } else {
                    double rate = Double.parseDouble(String.valueOf(editServiceRate.getText().toString().trim()));
                    updateService(productID, name, rate, req);
                    b.dismiss();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editServiceName.getText().toString().trim();
                deleteService(productID);
                b.dismiss();
            }
        });
    }

    //private void fieldsEmptyMessage(){Toast.makeText(this, "All fields need to be filled out", Toast.LENGTH_LONG).show();}

    private void updateService(String id, String name, double rate, String req){
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Service").child(id);

        Service service = new Service(id, name, rate, req);
        dr.setValue(service);

        Toast.makeText(this, "Service updated", Toast.LENGTH_LONG).show();
    }

    private void deleteService(String id){
        DatabaseReference dr = FirebaseDatabase.getInstance().getReference("Service").child(id);

        dr.removeValue();
        Toast.makeText(this, "Service deleted", Toast.LENGTH_LONG).show();
    }

    //Method to show service creation window
    private void showCreateServiceDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.create_service, null);
        dialogBuilder.setView(dialogView);

        final Button btnCreate = (Button) dialogView.findViewById(R.id.btnCreateBranch);
        final EditText serviceName = (EditText) dialogView.findViewById(R.id.etServiceName);
        final EditText serviceFee = (EditText) dialogView.findViewById(R.id.etServiceFee);
        final EditText serviceRequirements = (EditText) dialogView.findViewById(R.id.etServiceRequirements);
        serviceRequirements.setMovementMethod(new ScrollingMovementMethod());
        final AlertDialog b = dialogBuilder.create();
        b.show();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = serviceName.getText().toString().trim();
                String req = serviceRequirements.getText().toString().trim();
                String rateString = serviceFee.getText().toString().trim();

                // Name must only contain letters (upper or lower case) or spaces
                boolean isNameValid = !name.isEmpty() && name.matches("^[A-Z|a-z| ]+$");
                // Requirements is only checked for being none empty (no need to add extra limitations)
                boolean isReqValid = !req.isEmpty();
                // Rate must be numeric (but have have a decimal number)
                boolean isRateValid = !rateString.isEmpty() && rateString.matches("^[0-9]+(\\.)?[0-9]*$");

                /*if (!name.isEmpty() && !req.isEmpty() && !serviceFee.getText().toString().trim().isEmpty()){*/
                if (!isNameValid) {
                    Toast.makeText(getApplicationContext(), "Name invalid - must only contain letters (upper or lower case) or spaces", Toast.LENGTH_LONG).show();
                } else if (!isReqValid) {
                    Toast.makeText(getApplicationContext(), "Requirements invalid - must not be empty", Toast.LENGTH_LONG).show();
                } else if (!isRateValid) {
                    Toast.makeText(getApplicationContext(), "Rate invalid - must be numeric (but have have a decimal number)", Toast.LENGTH_LONG).show();
                } else {
                    double fee = Double.parseDouble(String.valueOf(serviceFee.getText().toString()));
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Service");
                    String serviceID = databaseReference.push().getKey();
                    Service service = new Service(serviceID, name, fee, req);
                    databaseReference.child(serviceID).setValue(service);
                    Toast.makeText(getApplicationContext(), "Service Created", Toast.LENGTH_LONG).show();
                    b.dismiss();
                } /*else{
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
                }*/
            }
        });


    }
}
