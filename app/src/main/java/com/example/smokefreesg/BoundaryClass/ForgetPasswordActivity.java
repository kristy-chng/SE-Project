package com.example.smokefreesg.BoundaryClass;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smokefreesg.ControllerClass.ForgetPasswordController;
import com.example.smokefreesg.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    public static EditText EmailID;
    public static Button ConfirmEmailButton;
    public static FirebaseAuth mAuth;
    private ForgetPasswordController fc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // initialization of XML variables
        EmailID = (EditText) findViewById(R.id.emailText);
        ConfirmEmailButton = (Button) findViewById(R.id.confirmButton);

        // initilization of firestore variable
        mAuth = FirebaseAuth.getInstance();

        // initialization of controller
        fc = new ForgetPasswordController();

        // confirm button listener --> mechanism to send forget password email to user
        ConfirmEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc.confirmEmail(ForgetPasswordActivity.this);
            }
        });

    }
}