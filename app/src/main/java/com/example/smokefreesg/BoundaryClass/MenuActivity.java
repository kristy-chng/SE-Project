package com.example.smokefreesg.BoundaryClass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smokefreesg.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MenuActivity extends AppCompatActivity {

    private TextView userName1;
    private Button logoutButt;
    private TextView goalButt;
    private TextView trackingButt;
    private TextView challengeButt;
    private TextView DSAButt;
    FirebaseFirestore fstore;

    public static SharedPreferences sharedPreferences;
    public static final String mypreference = "loginPref";
    public SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        fstore = FirebaseFirestore.getInstance();
        userName1 = (TextView) findViewById(R.id.welcomeText);
        logoutButt = (Button) findViewById(R.id.logout_Button);
        goalButt = (TextView) findViewById(R.id.GoalSetting_Button);
        trackingButt = (TextView) findViewById(R.id.Tracking_Button);
        challengeButt = (TextView) findViewById(R.id.Challenge_Button);
        DSAButt = (TextView) findViewById(R.id.FSA_Button);


        // Accessing account's username in database and displaying on Menu Page
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DocumentReference documentReference = fstore.collection("userAccount").document(uid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String n = value.getString("userName");
                userName1.setText("Hello " + n + "!");

            }
        });

        logoutButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        goalButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intToGoal();
            }
        });

        trackingButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intToTracking();
            }
        });

        challengeButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intToChallenge();
            }
        });

        DSAButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intToDSA();
            }
        });

    }

     public void logOut(){
        sharedPreferences = getSharedPreferences(mypreference, 0);
        editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        FirebaseAuth.getInstance().signOut();
        Intent intToMain = new Intent(MenuActivity.this, LoginActivity.class);
        startActivity(intToMain);
        finish();
    }

    public void intToGoal(){
        Intent intToGoal = new Intent(MenuActivity.this, GoalSettingActivity.class);
        startActivity(intToGoal);
    }

    public void intToDSA(){
        Intent intToDSA = new Intent(MenuActivity.this, MapsActivity.class);
        startActivity(intToDSA);
    }

    public void intToChallenge(){
        Intent intToChallenge = new Intent(MenuActivity.this, AchieveActivity.class);
        startActivity(intToChallenge);
    }

    public void intToTracking(){
        Intent intToTrack = new Intent(MenuActivity.this, TrackingActivity.class);
        startActivity(intToTrack);
    }
}