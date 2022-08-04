package com.abdoulayeln.byblos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Button login;
    private EditText usernameView, passwordView;
    private TextView registerActivity;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (Button)findViewById(R.id.btnLogin);
        registerActivity = (TextView)findViewById(R.id.tvRegisterActivity);
        usernameView = (EditText)findViewById(R.id.etLogName);
        passwordView = (EditText)findViewById(R.id.etPassword);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                credValidate(usernameView, passwordView);
            }
        });

        //SEND US FROM LOGIN TO REGISTRATION PAGE
        registerActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    //METHOD USED TO VERIFY THAT THE USER INPUT MATCH A USER IN OUR DB
    private void credValidate(EditText usernameView, EditText passwordView){ //Verify user credentials using Firebase
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();
        if (username.equals("customer") && password.equals("customer")){
            usernameView.setText("");
            passwordView.setText("");
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);
        }
        if (username.equals("employee") && password.equals("employee")){
            usernameView.setText("");
            passwordView.setText("");
            Intent intent = new Intent(MainActivity.this, EmployeeActivity.class);
            startActivity(intent);
        }
        if (username.equals("admin") && password.equals("admin")){
            usernameView.setText("");
            passwordView.setText("");
            Intent intent = new Intent(MainActivity.this, AdministratorActivity.class);
            startActivity(intent);
        }else {
            firebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        //Get user type to know which activity to go to
                        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);

                                usernameView.setText("");
                                passwordView.setText("");

                                if (user.getUserType().equals("Byblos Employee")){
                                    Intent intent = new Intent(MainActivity.this, EmployeeActivity.class);
                                    startActivity(intent);
                                }else if (user.getUserType().equals("Customer")){
                                    Intent intent = new Intent(MainActivity.this, UserActivity.class);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}