package com.unca.android.uncacampusbreeze;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LoginService extends IntentService {

    private static final String TAG = "LoginService";
    private static final String ACTION_LOGIN = "com.unca.android.uncacampusbreeze.action.Login";

    public LoginService() {
        super("LoginService");
    }

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
        SharedPreferences credentials = getApplicationContext().getSharedPreferences("com.unca.android.uncacampusbreeze.credentials", Context.MODE_PRIVATE);
        String uid = credentials.getString("uid", null);

        if (uid == null) { // we have not been assigned a uid
            Task<HttpsCallableResult> createNewAccountTask = FirebaseFunctions.getInstance().getHttpsCallable("createNewAccount").call();

            try {
                HttpsCallableResult newUidFromServer = Tasks.await(createNewAccountTask);
                uid = (String) newUidFromServer.getData();
                credentials.edit().putString("uid", uid).apply();
            } catch (ExecutionException e) {
                Intent i = new Intent("logged_in_status");
                i.putExtra("Status", false);
                getApplicationContext().sendBroadcast(i);
            } catch (InterruptedException e) {
                Intent i = new Intent("logged_in_status");
                i.putExtra("Status", false);
                getApplicationContext().sendBroadcast(i);
            }
        }

        long timeOfTokenCreation = System.currentTimeMillis();
        long timeSinceCreation = 3301;
        while (true) { // run until app stops ????? I guess this is what happens????? UHHHHH
            if (timeSinceCreation > 3300) { // 55 minutes
                Map<String, Object> data = new HashMap<>();
                data.put("uid", uid);
                Task<HttpsCallableResult> createCustomTokenTask = FirebaseFunctions.getInstance().getHttpsCallable("createCustomTokenForUid").call(data);

                try {
                    HttpsCallableResult newTokenFromServer = Tasks.await(createCustomTokenTask);
                    Task<AuthResult> signIntoServerTask =  FirebaseAuth.getInstance().signInWithCustomToken((String) newTokenFromServer.getData());
                    Tasks.await(signIntoServerTask);
                    Intent i = new Intent("logged_in_status");
                    i.putExtra("Status", true);
                    getApplicationContext().sendBroadcast(i);
                    timeOfTokenCreation = System.currentTimeMillis();
                } catch (ExecutionException e) {
                    Intent i = new Intent("logged_in_status");
                    i.putExtra("Status", false);
                    getApplicationContext().sendBroadcast(i);
                } catch (InterruptedException e) {
                    Intent i = new Intent("logged_in_status");
                    i.putExtra("Status", false);
                    getApplicationContext().sendBroadcast(i);
                }
            }

            SystemClock.sleep(100);
            timeSinceCreation = (long) ((System.currentTimeMillis() - timeOfTokenCreation) / 1000);
        }
    }
}

