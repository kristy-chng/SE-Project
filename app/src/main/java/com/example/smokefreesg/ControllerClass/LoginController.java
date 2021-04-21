package com.example.smokefreesg.ControllerClass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smokefreesg.BoundaryClass.LoginActivity;
import com.example.smokefreesg.BoundaryClass.MenuActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginController extends LoginActivity {

    // function for the log in authentication mechanism
    public void logIn(final Context context) {
        final String email = EmailID.getText().toString().trim();
        final String pw = Password.getText().toString().trim();

        if (email.isEmpty()) {
            EmailID.setError("Please enter an email.");
            EmailID.requestFocus();
        }

        else if (!email.contains("@")){
            EmailID.setError("Please enter a valid email address.");
            EmailID.requestFocus();

        } else if (pw.isEmpty()) {
            Password.setError("Please enter a password");
            Password.requestFocus();

        } else if (!(email.isEmpty() && pw.isEmpty()) && (email.contains("@"))) {
            mFirebaseAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(context, "Login Error - Incorrect Email/Password!", Toast.LENGTH_SHORT).show();
                    } else {
                        editor.putString(pEmail, email);
                        editor.putString(pPassword, pw);
                        editor.commit();
                        Toast.makeText(context, "Login Success - Welcome!", Toast.LENGTH_SHORT).show();
                        Intent intToHome = new Intent(context, MenuActivity.class);
                        context.startActivity(intToHome);
                    }
                }
            });
        }
    }
}
