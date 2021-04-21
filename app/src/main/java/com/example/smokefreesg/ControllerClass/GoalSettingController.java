package com.example.smokefreesg.ControllerClass;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smokefreesg.BoundaryClass.GoalSettingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;

public class GoalSettingController extends GoalSettingActivity {

    Calendar cal = Calendar.getInstance();
    int currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
    String currentWeek_s = Integer.toString(currentWeek);

    // function to get user input and save them into the database
    public void saveGoalData(Context context) {
        String goalCigNum_inputString = Q4_input.getText().toString().trim();
        String goalSpend_inputString = Q6_input.getText().toString().trim();

        if (!goalCigNum_inputString.isEmpty()) {
            final int goalCigNum_input = Integer.parseInt(goalCigNum_inputString);
            documentReference.update("goalCigNum", goalCigNum_input);

            documentReference2 = documentReference.collection("cigsData").document(currentWeek_s);
            documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        // If document with the week number already exists, cigsLeft value = weekly goal * cigs already smoked this week
                        if (doc.exists()) {
                            int cigs_int = doc.getLong("noOfCigs").intValue();
                            documentReference.update("cigsLeft", (goalCigNum_input * 7) - cigs_int);
                        }
                        // If document with the week number does not exist, cigsLeft for this week can be the goal number of cigs
                        else{
                            documentReference.update("cigsLeft", (goalCigNum_input * 7));
                        }
                    }
                }
            });
        }

        if (!goalSpend_inputString.isEmpty()) {
            int goalSpend_input = Integer.parseInt(goalSpend_inputString);
            documentReference.update("goalSpend", goalSpend_input);
        }

        if (goalCigNum_inputString.isEmpty() && goalSpend_inputString.isEmpty()) {
            Toast.makeText(context, "No Input - Nothing has been updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Your information has been updated", Toast.LENGTH_SHORT).show();
        }
    }

    // function to get user's current data and display them as a hint in EditText
    public void showCurrentData(){
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                int d = document.getLong("goalCigNum").intValue();
                int f = document.getLong("goalSpend").intValue();

                String dString = Integer.toString(d);
                String fString = Integer.toString(f);

                Q4_input.setHint(dString);
                Q6_input.setHint(fString);

            }
        });
    }


}