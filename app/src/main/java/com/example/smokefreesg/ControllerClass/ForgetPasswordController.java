package com.example.smokefreesg.ControllerClass;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smokefreesg.BoundaryClass.LoginActivity;
import com.example.smokefreesg.BoundaryClass.ForgetPasswordActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ForgetPasswordController extends ForgetPasswordActivity {

    // function to send an email to the user if user forgets password
    public void confirmEmail(final Context context){
        String email = EmailID.getText().toString().trim();

        if (email.isEmpty()){
            EmailID.setError("Please enter an email");
            EmailID.requestFocus();
        }
        else{
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(context, "An email has been sent to you for further action.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context, LoginActivity.class));
                    }
                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(context, "Error Occurred: "+ message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}
