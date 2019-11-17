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
import com.unca.android.uncacampusbreeze.server.Authorization;

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
        setServerAuthProgress(true);
        if (Authorization.signInToServer(this)) {
            setServerAuthProgress(false);
            Toast toast = Toast.makeText(getActivity(), "Successful withj signin to server.", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            setServerAuthProgress(false);
//            Toast toast = Toast.makeText(getActivity(), "...UNsuccessful withj signin to server.", Toast.LENGTH_SHORT);
//            toast.show();
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

}

