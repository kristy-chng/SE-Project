package com.example.smokefreesg.ControllerClass;

import com.example.smokefreesg.BoundaryClass.AchieveActivity;


public class AchieveController extends AchieveActivity {

    // function to check if a user has exceeded the goal
    public static void checkIfExceed(int cigs, int goalCigs_int) {
        if (cigs > goalCigs_int) {
            AchieveActivity.getInstance().smokerAlert();        }
    }

    public static void checklevel(int level, int level2)
    {
        if(level!= 1)
            if(level > level2)
                AchieveActivity.getInstance().levelAlert();
    }


}
