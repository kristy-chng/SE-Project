package com.example.smokefreesg.BoundaryClass;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smokefreesg.ControllerClass.GoalSettingController;
import com.example.smokefreesg.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class GoalSettingActivity extends AppCompatActivity {

    public static EditText Q4_input;
    public static EditText Q6_input;
    private Button saveButt;
    public static DocumentReference documentReference;
    public static DocumentReference documentReference2;
    private GoalSettingController gc;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_setting);

        // initialization of controller
        gc = new GoalSettingController();

        // initialization of  XML variables
        Q4_input = (EditText) findViewById(R.id.InputQ4);
        Q6_input = (EditText) findViewById(R.id.InputQ6);
        saveButt = (Button) findViewById(R.id.saveButton);

        // initialization of firestore variable
        fstore = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        documentReference = fstore.collection("userAccount").document(uid);

        // to show the current values in the database as a hint
        gc.showCurrentData();

        // save button listener --> to save user input into the database
        saveButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gc.saveGoalData(GoalSettingActivity.this);
            }
        });
    }

}