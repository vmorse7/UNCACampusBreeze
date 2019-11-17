package com.unca.android.uncacampusbreeze;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class LoungeActivity extends Activity {

    private static final String TAG = "LoungeActivity";

    private String mMyUid;

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

        Log.d(TAG, "Authorizing app with server...");
        // first we read from a file for a id number
        SharedPreferences credentialsSharedPref = getActivity().getSharedPreferences("com.unca.android.uncacampusbreeze.credentials", Context.MODE_PRIVATE);
        mMyUid = credentialsSharedPref.getString("uid", null);
        if (mMyUid == null) { // if the app instance has never recieved an id from  server
            Log.d(TAG, "No uid exists on phone. Requesting new one from server...");

            Toast toast = Toast.makeText(getActivity(), "Registering device with server. Getting a new uid", Toast.LENGTH_SHORT);
            toast.show();

            setServerAuthProgress(true);

            requestNewUid()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Exception e = task.getException();
                                if (e instanceof FirebaseFunctionsException) {
                                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                    FirebaseFunctionsException.Code code = ffe.getCode();
                                    Object details = ffe.getDetails();
                                }
                                Log.d(TAG, "requestNewUid:onFailure", e);
                                Toast toast = Toast.makeText(getActivity(), "Error with registering device.", Toast.LENGTH_SHORT);
                                toast.show();
                                setServerAuthProgress(false);
                                return;
                            } else {
                                Log.d(TAG, "requestNewUid:onSuccess");
                                String result = task.getResult();
                                setServerAuthProgress(false);
                                mMyUid = result;
                                Toast toast = Toast.makeText(getActivity(), "Got a new uid.", Toast.LENGTH_SHORT);
                                toast.show();
                                saveNewUidToDevice(mMyUid);
                            }
                        }
                    });
        } else {
            setServerAuthProgress(false);
//            Context context = getActivity();
//            CharSequence text = "Hello toast!";
//            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getActivity(), "Device already registered. Hello " + mMyUid, Toast.LENGTH_SHORT);
            toast.show();
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


    private void setServerAuthProgress(boolean isVisible) {
        ProgressBar centerLoadingWheel = findViewById(R.id.serverAuthLoadingWheel);
        centerLoadingWheel.setVisibility(View.INVISIBLE); // initialize to user as not loading anything

        if (isVisible) {
            centerLoadingWheel.setVisibility(View.VISIBLE);
        } else {
            centerLoadingWheel.setVisibility(View.INVISIBLE);
        }
    }

    private void saveNewUidToDevice(String newUid) {
        SharedPreferences credentialsSharedPref = getActivity().getSharedPreferences("com.unca.android.uncacampusbreeze.credentials", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = credentialsSharedPref.edit();
        editor.putString("uid", newUid);
        editor.apply();
    }
}

