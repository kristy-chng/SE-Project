package com.example.smokefreesg.ControllerClass;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smokefreesg.BoundaryClass.TrackingActivity;
import com.example.smokefreesg.EntityClass.MoneyData;
import com.example.smokefreesg.EntityClass.SmokingData;
import com.example.smokefreesg.EntityClass.SmokingDataPerDate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Map;

public class TrackingController extends TrackingActivity {

    // function to get and display the user's goal number of cigs per day
    private void getGoalCigNum() {
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                goal_cigs_int = document.getLong("cigsLeft").intValue(); // cigsLeft--> number of cigs left for the week
                goalCigs_line = document.getLong("goalCigNum").intValue(); // goal number of cigs per day
                goalAmount_line = document.getLong("goalSpend").intValue(); // goal amount of money spend per day
                goal_string = Integer.toString(goalCigs_line);
                aim.setText("You aim to smoke " + goal_string + " cigarettes/day.");
            }
        });
    }

    // function to get the user's weekly cigs left to stay within goal + reset weekly cigs goal if new week and display this change
     public void getWeeklyCigNum(){
        getGoalCigNum();
        documentReference5 = documentReference.collection("cigsData").document(currentWeek_s);
        documentReference5.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    // If document with the week number already exists, we update to display the total number of cigs for that day
                    if (doc.exists()) {
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot document2 = task.getResult();
                                int cigs_int = document2.getLong("cigsLeft").intValue();

                                if (cigs_int >=0) {
                                    String goal_cigs_String = Integer.toString(cigs_int);
                                    goal_noOfCigars.setText("To stay within your goal, \n number of cigarettes left this week is " + goal_cigs_String + ".");
                                }
                                else if (cigs_int < 0 ){
                                    int cigs_int2 = -(cigs_int);
                                    String cigs_String2 = Integer.toString(cigs_int2);
                                    goal_noOfCigars.setText("You have exceeded your weekly goal \n by " + cigs_String2 + " cigarettes. Try again next week!");
                                }
                            }
                        });
                    }

                    // if document with that week number does not exist, probably means its a new week so we reset the display to show weekly goal cig amount
                    else if (!doc.exists()) {
                        final int newcigs = goalCigs_line * 7;
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                documentReference.update("cigsLeft", newcigs);
                                String goal_cigs_String2 = Integer.toString(newcigs);
                                goal_noOfCigars.setText("To stay within your goal, \n number of cigarettes left this week is " + goal_cigs_String2 + ".");
                            }
                        });
                    }
                }
            }
        });
    }

    // function to decrease the number of cigs for the user whenever user enters the number of cigarettes smoked
    public void decreaseGoalCigNum(final Integer num) {
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                int cigs_left_int = document.getLong("cigsLeft").intValue();
                int cigs_left_week = (cigs_left_int - num);

                documentReference.update("cigsLeft", cigs_left_week);

                if (cigs_left_week >=0) {
                    String cigs_left_week_String = Integer.toString(cigs_left_week);
                    goal_noOfCigars.setText("To stay within your goal, \n number of cigarettes left this week is " + cigs_left_week_String + ".");
                }

                else if (cigs_left_week <0){
                    int cigs_left_week2 = -(cigs_left_week);
                    String cigs_left_week_String2 = Integer.toString(cigs_left_week2);
                    goal_noOfCigars.setText("You have exceeded your weekly goal \n by " + cigs_left_week_String2 + " cigarettes. Try again next week!");
                }

            }
        });
    }

    // function to get coordinates for the cigarette graph (retrieve data from database to get points)
    private void cigsDataToDictMap(final Map<Integer, Integer> map) {
        CollectionReference collectionReference = documentReference.collection("cigsData");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()){
                        int cigs_integer = document.getLong("noOfCigs").intValue();
                        int week_integer = document.getLong("weekNumber").intValue();
                        map.put(week_integer, cigs_integer);
                    }
                }

                //add graph data
                DataPoint[] values3 = new DataPoint[52];
                int goalCigs_line2 = goalCigs_line *7;
                for (int i = 0; i < values3.length; i++){
                    DataPoint y = new DataPoint(i,goalCigs_line2);
                    values3[i] = y;
                }

                LineGraphSeries<DataPoint> series3 = new LineGraphSeries<>(values3);
                cigarGraph.addSeries(series3);
                BarGraphSeries<DataPoint> series = new BarGraphSeries<>(getCigsPoint());
                cigarGraph.addSeries(series);

                series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                    @Override
                    public int get(DataPoint data) {
                        return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
                    }
                });
                series.setSpacing(10);
                series.setDrawValuesOnTop(true);
                series.setValuesOnTopColor(Color.RED);
                map.clear();
            }
        });
    }

    // transform the cigsDict into a compatible data structure to add into cig graph
    private DataPoint[] getCigsPoint() {
        DataPoint[] values = new DataPoint[getCigarDict.size()];
        int counter = 0;
        for (Map.Entry<Integer, Integer> entry: getCigarDict.entrySet()){
            DataPoint x = new DataPoint(entry.getKey(), entry.getValue());
            values[counter] = x;
            counter++;
        }
        return values;
    }

    // function to allow user to enter the number of cigs + update in the database
    public void addCigs(final Integer num) {
        // adding cigarette data into database based on week number (for the graph)
        final SmokingData smokingData = new SmokingData(num, currentWeek);
        documentReference2 = documentReference.collection("cigsData").document(currentWeek_s);
        documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // If document with the date already exists, we update the total number of cigs for that day
                    if (document.exists()) {
                        int cigsNumber = document.getLong("noOfCigs").intValue();
                        int new_cigsNumber = cigsNumber + num;
                        documentReference2.update("noOfCigs", new_cigsNumber);
                    }

                    // If document with the date does not exist, we create a new document for it
                    else {
                        documentReference2.set(smokingData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                    }
                }

            }
        });

        // adding cigarette data into database based on date (for Challenge part of the app)
        final SmokingDataPerDate smokingDataPerDate = new SmokingDataPerDate(num, false);
        documentReference4 = documentReference.collection("cigData-Date").document(mdate);
        documentReference4.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document1 = task.getResult();
                    // If document with the date already exists, we update the total number of cigs for that day
                    if (document1.exists()) {
                        int cigsNumber = document1.getLong("noOfCigs").intValue();
                        int new_cigsNumber = cigsNumber + num;
                        documentReference4.update("noOfCigs", new_cigsNumber);
                    }

                    // If document with the date does not exist, we create a new document for it
                    else {
                        documentReference4.set(smokingDataPerDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                    }
                }
            }
        });
    }

    // function to allow user to add in the amount of money spent
    public void addMoney(final float moneyAmount) {
        // getting current week of the year based on the current date
        final MoneyData moneyData = new MoneyData(moneyAmount,currentWeek);
        documentReference3 = documentReference.collection("moneyData").document(currentWeek_s);
        documentReference3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // If document with the date already exists, we update the total number of cigs for that day
                    if (document.exists()) {
                        float amountNumber = document.getLong("totalAmount").floatValue();
                        float new_amountNumber = amountNumber + moneyAmount;
                        documentReference3.update("totalAmount", new_amountNumber);
                    }

                    // If document with the date does not exist, we create a new document for it
                    else {
                        documentReference3.set(moneyData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }
                        });
                    }
                }
            }

        });
    }

    // function to get the coordinates for the money graph (retrieve from database) + show the graph
    private void moneyDataToDictMap(final Map<Integer, Float> map) {
        CollectionReference collectionReference2 = documentReference.collection("moneyData");
        collectionReference2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (DocumentSnapshot document : task.getResult()){
                        float amount_integer = document.getLong("totalAmount").floatValue();
                        int week_integer = document.getLong("weekNumber").intValue();
                        map.put(week_integer, amount_integer);
                    }
                }

                //add graph data
                DataPoint[] values4 = new DataPoint[52];
                for (int i = 0; i < values4.length; i++){
                    DataPoint y = new DataPoint(i,goalAmount_line);
                    values4[i] = y;
                }

                LineGraphSeries<DataPoint> series4 = new LineGraphSeries<>(values4);
                moneyGraph.addSeries(series4);
                BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>(getMoneyPoint());
                moneyGraph.addSeries(series2);

                series2.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                    @Override
                    public int get(DataPoint data) {
                        return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
                    }
                });
                series2.setSpacing(10);
                series2.setDrawValuesOnTop(true);
                series2.setValuesOnTopColor(Color.RED);
                map.clear();
            }
        });
    }

    // transform the moneyDict into a compatible data structure to add into money graph
    private DataPoint[] getMoneyPoint() {
        DataPoint[] values2 = new DataPoint[getAmountDict.size()];
        int counter = 0;
        for (Map.Entry<Integer, Float> entry: getAmountDict.entrySet()){
            DataPoint x = new DataPoint(entry.getKey(), entry.getValue());
            values2[counter] = x;
            counter++;
        }
        return values2;
    }

    // function for the refresh button
    public void refreshGraph(Context context){
        cigarGraph.removeAllSeries();
        cigsDataToDictMap(getCigarDict);
        moneyGraph.removeAllSeries();
        moneyDataToDictMap(getAmountDict);
        Toast.makeText(context, "Graph Updated", Toast.LENGTH_LONG).show();
    }

    // graph initialization
    public void displayGraphs (){
        // To display Cigar Graph
        cigsDataToDictMap(getCigarDict);
        cigarGraph.setTitle("Cigarettes Smoked /week");
        cigarGraph.getGridLabelRenderer().setVerticalAxisTitle("No. of Cigs");
        cigarGraph.getGridLabelRenderer().setHorizontalAxisTitle("Week");
        cigarGraph.getViewport().setXAxisBoundsManual(true);
        cigarGraph.getViewport().setMinX(40);
        cigarGraph.getViewport().setMaxX(52);
        cigarGraph.getViewport().setYAxisBoundsManual(true);
        cigarGraph.getViewport().setMinY(0);
        cigarGraph.getViewport().setMaxY(30);
        cigarGraph.getViewport().setScrollable(true);
        cigarGraph.getViewport().setScrollableY(true);
        cigarGraph.getViewport().setScalable(true);
        cigarGraph.getViewport().setScalableY(true);

        // To display Money Graph
        moneyDataToDictMap(getAmountDict);
        moneyGraph.setTitle("Money Spent on Cigs /week");
        moneyGraph.getGridLabelRenderer().setVerticalAxisTitle("Amount ($)");
        moneyGraph.getGridLabelRenderer().setHorizontalAxisTitle("Week");
        moneyGraph.getViewport().setXAxisBoundsManual(true);
        moneyGraph.getViewport().setMinX(40);
        moneyGraph.getViewport().setMaxX(52);
        moneyGraph.getViewport().setYAxisBoundsManual(true);
        moneyGraph.getViewport().setMinY(0);
        moneyGraph.getViewport().setMaxY(30);
        moneyGraph.getViewport().setScrollable(true);
        moneyGraph.getViewport().setScrollableY(true);
        moneyGraph.getViewport().setScalable(true);
        moneyGraph.getViewport().setScalableY(true);
    }

}
