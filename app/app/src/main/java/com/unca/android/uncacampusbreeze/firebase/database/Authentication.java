package com.unca.android.uncacampusbreeze.firebase.database;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;

public class Authentication {
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseUser currentUser = null;

    /*
        Intention is for this to be called onCreate().
        https://firebase.google.com/docs/auth/android/anonymous-auth
     */
    public static void loginToServer() {
        /*
            Whats going on here?
            .signInAnonymously():
                this creates a new account in Firebase Auth system unless there is already an anon
                user signed into the app.
                This will return a Task<AuthResult> sooo task will return a AuthResult when it succeeds.
            .addOnCompleteListener():
                Task is an api that represents asynchronous method calls. So to be notified when the task
                succeeds, we can attach a listener. We will handle succees and failure in the same listener.
        */
        mAuth.signInAnonymously().addOnCompleteListener(
            new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        currentUser = mAuth.getCurrentUser();
                    } else {
                        Log.e("Authentication", "signInAnonmously:failure", task.getException());
                    }
                }
            }
        );
    }
}
