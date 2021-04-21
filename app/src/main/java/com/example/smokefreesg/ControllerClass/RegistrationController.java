package com.example.smokefreesg.ControllerClass;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.smokefreesg.BoundaryClass.LoginActivity;
import com.example.smokefreesg.BoundaryClass.RegistrationActivity;
import com.example.smokefreesg.EntityClass.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationController extends RegistrationActivity {

    // function to add user data into the database
    private void addUserAccount(final String username, final String email, final String pw, final Context context) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser rUser = mFirebaseAuth.getCurrentUser();
                    final String userId = rUser.getUid();
                    documentReference = fstore.collection("userAccount").document(userId);
                    UserAccount user1 = new UserAccount(username, email, 0,0,0,0,0,0);
                    documentReference.set(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "User Account Created!", Toast.LENGTH_SHORT).show();
                            context.startActivity(new Intent(context, LoginActivity.class));
                        }
                    });
                }
                else if (!task.isSuccessful()){
                    FirebaseAuthException e = (FirebaseAuthException)task.getException();
                    Toast.makeText(context,"Failed Registration: "+ e.getMessage() , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // function to add the user information into firebase authentication
    public void createAccount(Context context){
        final String email = EmailID.getText().toString().trim();
        final String pw = Password.getText().toString().trim();
        final String username = UserID.getText().toString().trim();

        if (username.isEmpty()) {
            UserID.setError("Please enter a username");
            UserID.requestFocus();

        } else if (username.length() > 10) {
            UserID.setError("Ensure that username is within 10 characters.");
            UserID.requestFocus();

        }
        else if (email.isEmpty()) {
            EmailID.setError("Please enter an email.");
            EmailID.requestFocus();

        } else if (!email.contains("@")){
            EmailID.setError("Please enter a valid email address.");
            EmailID.requestFocus();

        } else if (pw.isEmpty()) {
            Password.setError("Please enter a password");
            Password.requestFocus();

        } else if (!AgeCheck.isChecked()) {
            Toast.makeText(context, "Please declare that you are over 18!", Toast.LENGTH_SHORT).show();

        } else if (!(email.isEmpty() && pw.isEmpty() && username.isEmpty()) && AgeCheck.isChecked() && email.contains("@") && username.length()<10) {
            addUserAccount(username, email, pw, context);

        } else
        {
            Toast.makeText(context, "SignUp Unsuccessful - Ensure your password is >= 6 char!", Toast.LENGTH_SHORT).show();
        }
    }
}
