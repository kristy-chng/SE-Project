package com.example.smokefreesg.BoundaryClass;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smokefreesg.ControllerClass.AchieveController;
import com.example.smokefreesg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AchieveActivity extends AppCompatActivity {

    FirebaseFirestore fstore;
    public String uid;
    public DocumentReference documentReference;
    public DocumentReference documentReference2;
    public DocumentReference documentReference3;
    public CollectionReference collectionReference;
    public int goalCigs_int;
    public String goalCigs_String;
    public int currentWeek;
    public int points2 = 0;
    public int cigsToday_int;
    public String cigsToday_String;
    public String mdate;
    public TextView daysAchieved;
    public Date mdate2;

    public TextView cigsPerDay;
    public TextView userPoints;
    public TextView cigsLeftPerDay;
    public TextView currentlevel;
    public static final String MyPreferences = "prefs";
    private static AchieveActivity instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);
        instance = this;

        // getting current date and time values
        mdate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        try {
            mdate2 = new SimpleDateFormat("dd-MM-yyyy").parse(mdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        String currentWeek_s = Integer.toString(currentWeek);


        // initializing firestore database variables
        fstore = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        documentReference = fstore.collection("userAccount").document(uid);
        documentReference2 = documentReference.collection("cigData-Date").document(mdate);
        documentReference3 = documentReference.collection("cigsData").document(currentWeek_s);
        collectionReference = documentReference.collection("cigData-Date");

        // initializing textview variables
        cigsPerDay = (TextView) findViewById(R.id.cigLimit);
        cigsLeftPerDay = (TextView) findViewById(R.id.heading3);
        daysAchieved = (TextView) findViewById(R.id.noOfDaysAchieved);
        currentlevel = (TextView) findViewById(R.id.level);
        userPoints = (TextView) findViewById(R.id.subheading4);


        // function calls
        getCigsNumber(); // to get the goal number of cigarettes smoked per day via the firestore database (value is set in Goal Setting)
        getCigsNumToday(); // to get the number of cigarettes already smoked today (in case the user add cigs via the Tracking part)
        getPoints();
    }

    public static AchieveActivity getInstance() {
        return instance;
    }

    // function to get the goal number of cigs + display
    public void getCigsNumber() {
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                goalCigs_int = value.getLong("goalCigNum").intValue();
                goalCigs_String = Integer.toString(goalCigs_int);
                cigsPerDay.setText("Based on your goals, \n your cigarette limit per day is " + goalCigs_String);
            }
        });
    }

    // function to get the number of cigs smoked today + display
    public void getCigsNumToday() {
        documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document1 = task.getResult();
                    // If document exists, get the number of cigarettes smoke today
                    if (document1.exists()) {
                        cigsToday_int = document1.getLong("noOfCigs").intValue();
                        cigsToday_String = Integer.toString(cigsToday_int);
                        cigsLeftPerDay.setText("Number of cigarettes that you have smoked so far today is " + cigsToday_String);
                    }

                    // If document with the date does not exist, means user did not log any cigarettes for today (& no data in database)
                    else {
                        cigsLeftPerDay.setText("Number of cigarettes that you have smoked so far today is 0.");
                    }
                    AchieveController.checkIfExceed(cigsToday_int, goalCigs_int);
                    getDaysAchieved();
                }

            }
        });
    }

    // function to check how many days a user has stayed within his goal
    public void getDaysAchieved() {
        // entering database and listening to values
        documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document2 = task.getResult();
                    // check all of the user's cigarette data (based on the day)
                    if (document2.exists()) {
                        // check the number of cigs he has smoked for the day
                        int cigsToday = document2.getLong("noOfCigs").intValue();
                        // compare it to the goal number of cigs
                        if (cigsToday < goalCigs_int) {
                            // true if he stayed within the goal
                            documentReference2.update("goalCheck", true);
                        } else {
                            // false if he did not stay within the goal
                            documentReference2.update("goalCheck", false);
                        }
                    }

                    // checking the no of cigarettes a user smokes for all the days that were logged
                    collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int counter = 0;
                                for (QueryDocumentSnapshot document3 : task.getResult()) {
                                    boolean check = document3.getBoolean("goalCheck");
                                    // if goalCheck == true, means 1 day of user achieving his goal
                                    if (check == true) {
                                        counter = counter + 1;
                                    }
                                }
                                // display it on screen
                                daysAchieved.setText(counter + "\n Days");
                            }
                        }
                    });
                }
            }
        });
    }

    public void levelAlert() {
        final Dialog MyDialog2 = new Dialog(AchieveActivity.this);
        MyDialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog2.setContentView(R.layout.level_dialog);

        Button close = (Button) MyDialog2.findViewById(R.id.close);

        close.setEnabled(true);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog2.cancel();
            }
        });
        MyDialog2.show();
    }

    // function for the smoking alert dialog when user exceeds goal
    public void smokerAlert() {
        final Dialog MyDialog = new Dialog(AchieveActivity.this);
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.warning_dialog);

        Button close = (Button) MyDialog.findViewById(R.id.close);

        close.setEnabled(true);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.cancel();
            }
        });
        MyDialog.show();
    }


    public void getPoints() {

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int numDaysUsed = 0;
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        numDaysUsed++;
                    }

                    final int points = numDaysUsed * 10;


                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                points2 = (document.getLong("userPoints")).intValue();
                                if (points > points2){
                                    documentReference.update("userPoints", points);
                                }
                                getLevel();
                            }
                        }
                    });

                }
            }
        });
    }

    public void getLevel(){
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    int points2 = document.getLong("userPoints").intValue();
                    String points2_s = Integer.toString(points2);
                    userPoints.setText("Earn 10 points/day when you log in your smoking habits. (Current points: " + points2_s +")");

                    int level = 1 + points2/20;
                    String level_s = Integer.toString(level);
                    currentlevel.setText(level_s);

                    SharedPreferences prefs = getSharedPreferences(MyPreferences, MODE_PRIVATE);
                    final int level2 = prefs.getInt("level", 1); //1 is the default value.

                    AchieveController.checklevel(level, level2);

                    SharedPreferences.Editor editor = getSharedPreferences(MyPreferences, MODE_PRIVATE).edit();
                    editor.putInt("level",level);
                    editor.apply();


                    }
                }

        });
    }


}