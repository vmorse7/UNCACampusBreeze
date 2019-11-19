package com.unca.android.uncacampusbreeze;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableReference;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LoungeActivity extends Activity {

    final private static String TAG = "LoungeActivity";
    final private static String CREDENTIALS_FILE_UID_KEY = "uid";
    final private static String CREDENTIALS_FILE_NAME = "com.unca.android.uncacampusbreeze.credentials";

    private String muid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lounge);
        Log.d(TAG, "onCreate() being called.");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() being called.");

        SharedPreferences credentialsSharedPref = getActivity().getSharedPreferences(CREDENTIALS_FILE_NAME, Context.MODE_PRIVATE);
        String uid = credentialsSharedPref.getString(CREDENTIALS_FILE_UID_KEY, null);

        if (uid == null) { // assume device has never registered with server before.
            registerWithThenSignIntoServer();
        } else {
            signIntoServerWithUid(uid);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() being called.");


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() being called.");


    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() being called.");


    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart() being called.");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() being called.");


    }

    public Context getActivity() {
        return LoungeActivity.this;
    }

    private void registerWithThenSignIntoServer() {
        Toast toast = Toast.makeText(getActivity(), "Registering for the first time with server...", Toast.LENGTH_SHORT);
        toast.show();

        createNewAccount()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Toast toast = Toast.makeText(getActivity(), "Registration was a success!", Toast.LENGTH_SHORT);
                        toast.show();
                        getActivity()
                                .getSharedPreferences(CREDENTIALS_FILE_NAME, Context.MODE_PRIVATE)
                                .edit()
                                .putString(CREDENTIALS_FILE_UID_KEY, s)
                                .apply();

                        signIntoServerWithUid(s);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast toast = Toast.makeText(getActivity(), "Registration was a failure!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
    }

    private void signIntoServerWithUid(String uid) {
        Toast toast = Toast.makeText(getActivity(), "Signing into server...", Toast.LENGTH_SHORT);
        toast.show();

        createCustomTokenForUid(uid)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signInWithCustomToken(s)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast toast = Toast.makeText(getActivity(), "Sign in with server successful.", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast toast = Toast.makeText(getActivity(), "Sign in with server unsuccessful.", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                        Toast toast = Toast.makeText(getActivity(), "Sign in with server unsuccessful.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
    }

    private Task<String> createNewAccount() {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        return functions
                .getHttpsCallable("createNewAccount")
                .call()
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
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

