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

public class GatekeepingService extends IntentService {

    private static final String ACTION_GATEKEEP = "com.unca.android.uncacampusbreeze.action.GATEKEEP";


    private String mUid;
    private SharedPreferences mCredentials;

    public GatekeepingService() {
        super("GatekeepingService");
    }

    /*
        Starts the service to perform the action of gatekeeping. See the class description above
        for what this entails. But really there should not be multiple actions being queue
        as that seems kinda pointless and undefined. like an ant losing its colony, I wouldn't
        know what to do.
     */
    public static void startActionGatekeep(Context context, Intent gateStatus) {
        Intent intent = new Intent(context, GatekeepingService.class);
        intent.` `
        intent.setAction(ACTION_GATEKEEP);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GATEKEEP.equals(action)) {
                handleActionGatekeep();
            }
        }
    }

    /*
        Handle action Gatekeep in the provided background thread.
    */
    private void handleActionGatekeep() {
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
                            Log.d("GatekeepingActivity", "Phone has registered with server and got a new uid.");
                            mCredentials.edit() // store it for future use
                                    .putString("uid", s)
                                    .apply();
                            mUid = s; // store it for using it now
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() { // could not register with server
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("GatekeepingActivity", "Phone could not register with server.");
                        }
                    })
            ;

            while (mUid == null) { // tyhe above stuff (FF.getInst...().cal... runs asynchronlouly
                Log.d("GatekeepingActivity", "Waiting for the new uid to be sent to device by server....");
                SystemClock.sleep(1000); // so wait until mUid is set to something. Remember we are using an intentservice so we are NOT in UI thread<><><
            }
        }

        // we have a uid
        createCustomTokenForUid(mUid)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signInWithCustomToken(s)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Log.d("GatekeepingActivity", "Recieved a custom token from server and successfully used it to sign in to Firebase.");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("GatekeepingActivity", "Recieved a custom token from server but unsucessfully signed into firebase.");
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { // couldn't get a token from the server
                        Log.d("GatekeepingActivity", "Could not get a custom token from server.");
                    }
                });

        while (FirebaseAuth.getInstance().getCurrentUser() == null) { // wait unti lsuer is signed in..... ?????!??!
            Log.d("GatekeepingActivity", "Waiting to be signed into Firebase...");
            SystemClock.sleep(100);
            // this is leading to issue maybe because this is   bad way crude way of determining if authoirized with firebase... but we won't worry about it right now....
        }

        while (true) { // run until app stops ????? I guess this is what happens????? UHHHHH
            // TODO: Implement thing for hvaing the phone refresh token ever so n minutes... Token expires after 60 minutes sooo.
            Log.d("GatekeepingActivity", "Now I'm doing stuff !");
            SystemClock.sleep(1000); // avoids running cpu at 100%
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

