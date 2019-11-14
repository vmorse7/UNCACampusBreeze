package com.unca.android.uncacampusbreeze;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class LoungeActivity extends AppCompatActivity {

    private static final String TAG = "LoungeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lounge);
        Log.d(TAG, "onCreate() being called.");


        Log.d(TAG, "onCreate() has finished.");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() being called.");


        Log.d(TAG, "onStart() has finished.");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() being called.");


        Log.d(TAG, "onResume() has finished.");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() being called.");


        Log.d(TAG, "onPause() has finished.");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() being called.");


        Log.d(TAG, "onStop() has finished.");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart() being called.");


        Log.d(TAG, "onRestart() has finished.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() being called.");


        Log.d(TAG, "onDestroy() has finished.");
    }
}

