/**
 * Purpose: Provide the code for registering a device to the server.
 */
package com.unca.android.uncacampusbreeze.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.unca.android.uncacampusbreeze.LoungeActivity;

import java.util.concurrent.atomic.AtomicReference;

public class Authorization {

    final private static String TAG = "Authorization";

    private static Authorization instance = null;

    public static boolean signInToServer(LoungeActivity activity) {
//        Toast toast = Toast.makeText(activity, "Registering device with server. Getting a new uid", Toast.LENGTH_SHORT);
//        toast.show();

        final String CREDENTIALS_FILE_NAME = "com.unca.android.uncacampusbreeze.credentials";
        final String CREDENTIALS_FILE_UID_INDEX = "uid";

        SharedPreferences credentialsSharedPref = activity.getSharedPreferences(CREDENTIALS_FILE_NAME, Context.MODE_PRIVATE);
        final AtomicReference<String> uid = new AtomicReference<>(credentialsSharedPref.getString(CREDENTIALS_FILE_UID_INDEX, null));

        if (uid.get() == null) { // device/app instance does not have prior history with server
            CloudFunctions.createNewAccount()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }
                            String result = task.getResult();
                            uid.set(result);
                        }
                    });
        }

        if (uid.get() == null) { // if there is still a problem
            return false;
        }

        SharedPreferences.Editor credentialsEditor = credentialsSharedPref.edit();
        credentialsEditor.putString(CREDENTIALS_FILE_UID_INDEX, uid.get());
        credentialsEditor.apply();

        final AtomicReference<String> customToken = new AtomicReference<>();
        CloudFunctions.getCustomToken(uid.get())
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String result = task.getResult();
                        customToken.set(result);
                    }
                });

        if (customToken.get() == null) {
            return false;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithCustomToken(customToken.get())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (!task.isSuccessful()) {
//                                Log.d(TAG, "signInWithCustomToken:failure", task.getException());
//                            }
//                            return
                    }
                });

        if (auth.getCurrentUser() == null) {
            return false;
        }

        return true;
    }

}
