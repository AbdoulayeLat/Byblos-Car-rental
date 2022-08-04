package com.abdoulayeln.byblos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText firstName, lastName, emailAdress, password, rePassword;
    Button register, sign_in;
    private FirebaseAuth firebaseAuth;
    private String userType;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        firstName = (EditText)findViewById(R.id.etBranchName);
        lastName = (EditText)findViewById(R.id.etBranchAddress);
        emailAdress = (EditText)findViewById(R.id.etBranchEmail);
        password = (EditText)findViewById(R.id.etPasswordReg);
        rePassword = (EditText)findViewById(R.id.etRePasswordReg);
        register = (Button)findViewById(R.id.btnRegister);
        sign_in = (Button)findViewById(R.id.btnSignIn);

        //SPINNER IS USED FOR THE DROPDOWN MENU SELECTION CONCERNING USER TYPE
        Spinner mySpinner = (Spinner)findViewById(R.id.spnUserType);
        ArrayAdapter<CharSequence> myAdapter = ArrayAdapter.createFromResource(this, R.array.Type, android.R.layout.simple_list_item_1);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);
        mySpinner.setOnItemSelectedListener(this);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GETTING USER INPUT FOR FIREBASE
                String email = emailAdress.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String rePass = rePassword.getText().toString();
                String fName = firstName.getText().toString().trim();
                String lName = lastName.getText().toString().trim();

                if (email.isEmpty() || pass.isEmpty() || rePass.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please enter all details", Toast.LENGTH_LONG).show();
                }else if(!pass.equals(rePass)){
                    Toast.makeText(RegisterActivity.this, "The passwords don't match!", Toast.LENGTH_LONG).show();
                }else if(!fName.matches("^[A-Z|a-z| ]+$") || !lName.matches("^[A-Z|a-z| ]+$")){
                    // check if the name is invalid due to containing numbers
                    Toast.makeText(RegisterActivity.this, "Name is invalid - must only contain letters (upper or lower case) or spaces", Toast.LENGTH_LONG).show();
                } else if(!email.matches("^(\\w)+@(\\w)+\\.(\\w)+$")){
                    // check if the email contains @
                    Toast.makeText(RegisterActivity.this, "Invalid e-mail - must be [string]@[string].[string]", Toast.LENGTH_LONG).show();
                } else{
                    //CREATING USER CREDENTIALS FOR LOGGING IN
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Created the user account", Toast.LENGTH_LONG).show();

                                //UPLOADING USER DATA TO THE DATABASE (NB: FIND A WAY TO VERIFY EMAIL AND PASSWORD FORMAT BEFORE UPLOAD!!
                                // ALSO MAKE SURE TO ADD USER TYPE TO DATA)

                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                User user = new User(uid, fName, lName, email, pass, userType);
                                databaseReference.child("User").child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(RegisterActivity.this, "Uploaded user data successfully", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }else{
                                            String message = task.getException().getMessage();
                                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }else{
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        userType = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}