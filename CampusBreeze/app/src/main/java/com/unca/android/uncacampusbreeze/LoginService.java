/*
    Author: N/A
    Date: 12/07/2019
    Description:

    The To-Do List:
 */

package com.unca.android.uncacampusbreeze;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Http;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class LoginService extends IntentService {

    private static final String ACTION_LOGIN = "com.unca.android.uncacampusbreeze.action.Login";
    private static final String TAG = "LoginService";

    private String mUid;
    private SharedPreferences mCredentials;
    private boolean mLoungeLockedToScreen = true;
    private long mTimeOfTokenCreation = System.currentTimeMillis();

    public LoginService() {
        super("LoginService");
    }

    /*
        Starts the service to perform the action of loggin in. See the class description above
        for what this entails. But really there should not be multiple actions being queue
        as that seems kinda pointless and undefined. like an ant losing its colony, I wouldn't
        know what to do.
     */
    public static void startActionLogin(Context context) {
        Intent intent = new Intent(context, LoginService.class);
        intent.setAction(ACTION_LOGIN);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOGIN.equals(action)) {
                handleActionLogin();
            }
        }
    }

    /*
        Handle action Login in the provided background thread.
    */
    private void handleActionLogin() {
        mCredentials = getApplicationContext().getSharedPreferences("com.unca.android.uncacampusbreeze.credentials", Context.MODE_PRIVATE);
        mUid = mCredentials.getString("uid", null);

        if (mUid == null) { // we have not been assigned a uid
            FirebaseFunctions.getInstance()
                    .getHttpsCallable("createNewAccount")
                    .call()
                    .continueWith(new Continuation<HttpsCallableResult, String>() {
                        @Override
                        public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                            String result = (String) task.getResult().getData();
                            return result; // the uid from the server to assign to the phone
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<String>() { // registered with server and got a uid
                        @Override
                        public void onSuccess(String s) {
                            Log.d(TAG, "Phone has registered with server and got a new uid.");
                            mCredentials.edit() // store it for future use
                                    .putString("uid", s)
                                    .apply();
                            mUid = s; // store it for using it now
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() { // could not register with server
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Intent i = new Intent("logged_in_status");
                            i.putExtra("Status", false);
                            getApplicationContext().sendBroadcast(i);
                        }
                    });

            while (mUid == null) { // tyhe above stuff (FF.getInst...().cal... runs asynchronlouly
                Log.d(TAG, "Waiting for the new uid to be sent to device by server....");
                SystemClock.sleep(100); //
            }
        }

        long timeSinceCreation = 3301;
        while (true) { // run until app stops ????? I guess this is what happens????? UHHHHH
            if (timeSinceCreation > 3300) {
                createCustomTokenForUid(mUid)
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                auth.signInWithCustomToken(s)
                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                Intent i = new Intent("logged_in_status");
                                                i.putExtra("Status", true);
                                                getApplicationContext().sendBroadcast(i);
                                                mTimeOfTokenCreation = System.currentTimeMillis();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Intent i = new Intent("logged_in_status");
                                                i.putExtra("Status", false);
                                                getApplicationContext().sendBroadcast(i);
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) { // couldn't get a token from the server
                                Intent i = new Intent("logged_in_status");
                                i.putExtra("Status", false);
                                getApplicationContext().sendBroadcast(i);
                            }
                        });
            }

            SystemClock.sleep(10000);
            timeSinceCreation = (long) ((System.currentTimeMillis() - mTimeOfTokenCreation) / 1000);
        }
    }

    private Task<String> createCustomTokenForUid(String uid) {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", uid);

        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        return functions
                .getHttpsCallable("createCustomTokenForUid")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }
}

