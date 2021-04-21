package com.example.smokefreesg.BoundaryClass;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smokefreesg.ControllerClass.TrackingController;
import com.example.smokefreesg.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jjoe64.graphview.GraphView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TrackingActivity extends AppCompatActivity {

    //To access database data (retrieve the number of cigars per day/week)
    public static FirebaseFirestore fstore;
    public static TextView goal_noOfCigars;
    public static String uid;
    public static int goalCigs_line;
    public static int goalAmount_line;
    public static int goalCigs_line2;
    public static String goal_string;
    public static TextView aim;

    public static DocumentReference documentReference;
    public static DocumentReference documentReference2;
    public static DocumentReference documentReference3;
    public static DocumentReference documentReference4;
    public static DocumentReference documentReference5;


    public static Map <Integer, Integer> getCigarDict = new HashMap<Integer, Integer>();
    public static Map <Integer, Float> getAmountDict = new HashMap<Integer, Float>();
    private Button refreshButt;
    public static String mdate;
    public static int currentWeek;
    public static String currentWeek_s;
    public static int goal_cigs_int;

    public static GraphView cigarGraph;
    public static GraphView moneyGraph;
    private ImageButton cigarettebtn;
    private ImageButton moneybtn;
    private String moneytext;
    private String cigartext;
    private Integer cigartext2;
    private ArrayList<Integer> cigarlist = new ArrayList<Integer>();

    public TrackingController tc;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        // initialization of week and date variables
        mdate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Calendar cal = Calendar.getInstance();
        currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        currentWeek_s = Integer.toString(currentWeek);

        // initialization of firestore-related variables
        fstore = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        documentReference = fstore.collection("userAccount").document(uid);

        // initialization of controller class
        tc = new TrackingController();

        // initialization of textview
        goal_noOfCigars = (TextView) findViewById(R.id.noOfCigsLeft);
        aim = (TextView) findViewById(R.id.heading1);

        // function call to display text on the textviews on screen
        tc.getWeeklyCigNum();

        // refresh button
        refreshButt = (Button) findViewById(R.id.refreshButton);
        refreshButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tc.refreshGraph(TrackingActivity.this);
            }
        });

        // to display graph
        cigarGraph = (GraphView) findViewById(R.id.cigarGraph);
        moneyGraph = (GraphView) findViewById(R.id.moneyGraph);
        tc.displayGraphs();

        // cigarette button listener
        cigarettebtn = (ImageButton) findViewById((R.id.addcigarbtn));
        cigarettebtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(TrackingActivity.this);
                dialog.setTitle("Enter number of cigarettes smoked today for tracking \n" + "Use a negative sign if you wish to remove cigarettes");

                final EditText cigarinput = new EditText(TrackingActivity.this);
                cigarinput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                dialog.setView(cigarinput);

                dialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cigartext = cigarinput.getText().toString();
                        if (!cigartext.isEmpty()) {
                            cigartext2 = Integer.parseInt(cigartext);
                            cigarlist.add(cigartext2);
                            Toast.makeText(TrackingActivity.this, "Cigarette smoked today is " + cigartext2, Toast.LENGTH_LONG).show();
                            // Decrease cigarettes in the database and display the decrease
                            tc.decreaseGoalCigNum(cigartext2);
                            tc.addCigs(cigartext2);
                        }
                        else{
                            Toast.makeText(TrackingActivity.this, "Please enter a number.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                dialog.show();
            }
        }));

        // money button listener
        moneybtn = (ImageButton) findViewById(R.id.addmoneybtn);
        moneybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog2 = new AlertDialog.Builder(TrackingActivity.this);
                dialog2.setTitle("Enter money spent this week ");

                final EditText moneyinput = new EditText(TrackingActivity.this);
                moneyinput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                dialog2.setView(moneyinput);

                dialog2.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        moneytext = moneyinput.getText().toString();
                        if (!moneytext.isEmpty()) {
                            Float moneytext2 = Float.parseFloat(moneytext);
                            Toast.makeText(TrackingActivity.this, "Money spent this week is $" + moneytext2, Toast.LENGTH_LONG).show();
                            tc.addMoney(moneytext2);
                        }
                        else{
                            Toast.makeText(TrackingActivity.this, "Please enter a number.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                dialog2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                dialog2.show();
            }
        });

    }

}





