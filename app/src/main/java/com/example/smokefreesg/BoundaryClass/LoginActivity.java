package com.example.smokefreesg.BoundaryClass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smokefreesg.ControllerClass.LoginController;
import com.example.smokefreesg.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // Declaration of XML variables
    public static EditText EmailID;
    public static EditText Password;
    public static TextView createAccount;
    private Button loginButton;
    private TextView forgetPWButton;
    private LoginController lc;

    // Declaration of firebase utilities
    public static FirebaseAuth mFirebaseAuth;

    // Declaration of SharedPreferences variables and utilities
    public static SharedPreferences.Editor editor;
    public static SharedPreferences sharedPreferences;
    public static String mypreference = "loginPref";
    public static String pEmail = "emailKey";
    public static String pPassword = "passwordKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialization of controller
        lc = new LoginController();

        // initialization of XML variables
        EmailID = (EditText) findViewById(R.id.emailText);
        Password = (EditText) findViewById(R.id.passwordText);
        mFirebaseAuth = FirebaseAuth.getInstance();
        loginButton = (Button) findViewById(R.id.loginButton);
        createAccount = (TextView) findViewById(R.id.registerButton);
        forgetPWButton = (TextView) findViewById(R.id.forgotPassword);

        // Initialization of shared preferences so that user does not always need to manually input credentials
        sharedPreferences = getSharedPreferences(mypreference, 0);
        editor = sharedPreferences.edit();

        if (sharedPreferences.contains(pEmail)) {
            EmailID.setText(sharedPreferences.getString(pEmail, ""));
        }
        if (sharedPreferences.contains(pPassword)) {
            Password.setText(sharedPreferences.getString(pPassword, ""));
        }

        // forget password button listener --> to redirect to forget password page
        forgetPWButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
                startActivity(i);
            }
        });

        // create account button listener --> to redirect to create account page
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(i);
            }
        });

        // log in button listener --> to redirect user to menu page if authentication is successful
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lc.logIn(LoginActivity.this);
            }
        });

    }
}