package com.unca.android.uncacampusbreeze;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.functions.FirebaseFunctions;
import com.unca.android.uncacampusbreeze.server.TokenService;

public class LoungeActivity extends Activity {

    private static final String TAG = "LoungeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lounge);
        Log.d(TAG, "onCreate() being called.");

        Log.d(TAG, "Authorizing app with server...");

        // first we read from a file for a id number
        SharedPreferences credentialsSharedPref = getActivity().getSharedPreferences("com.unca.android.uncacampusbreeze.credentials", Context.MODE_PRIVATE);
        String defaultUid = "NONE";
        String uidFromCredentials = credentialsSharedPref.getString("uid", defaultUid);
        if (uidFromCredentials == defaultUid) { // if the app instance has never recieved an id from  server
            // request new uid from server
//            new DownloadFilesTask().execute(url1, url2, url3);
            new NewUidTask().execute();
        } else {
            // authenticate with server
        }

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

    public Context getActivity() {
        return LoungeActivity.this;
    }

    private class NewUidTask extends AsyncTask<Void, Void, String> {

        private static final String TAG = "NewUidTask";

        @Override
        protected String doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground() being called.");

            String newUID;
            FirebaseFunctions functions;
            functions = FirebaseFunctions.getInstance();
            

            Log.d(TAG, "doInBackground() has finished.");
            return newUID;
        }

        protected void onPostExecute(String newUid) {
            Log.d(TAG, "onPostExecute() being called.");
            Log.d(TAG, "I recieved the uid " + newUid + " from the server.");
            Log.d(TAG, "onPostExecute() has finished.");
        }
    }
}

