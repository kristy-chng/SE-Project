package com.example.smokefreesg.BoundaryClass;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smokefreesg.ControllerClass.RegistrationController;
import com.example.smokefreesg.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationActivity extends AppCompatActivity {

    // Declaration of Variables
    public static EditText UserID;
    public static EditText EmailID;
    public static EditText Password;
    public static Button createAccount;
    public static CheckBox AgeCheck;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseFirestore fstore;
    public static DocumentReference documentReference;
    private RegistrationController rc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // initialization of XML variables
        UserID = (EditText) findViewById(R.id.usernameText);
        EmailID = (EditText) findViewById(R.id.emailText);
        Password = (EditText) findViewById(R.id.passwordText);
        AgeCheck = (CheckBox) findViewById(R.id.ageCheckbox);
        createAccount = (Button) findViewById(R.id.registerButton);

        // initialization of firestore variables
        mFirebaseAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        // initialization of controller
        rc = new RegistrationController();

        // create account button listener --> to create the account for the user based on input
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rc.createAccount(RegistrationActivity.this);
            }
        });
    }
}

