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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
        new SignInTask().execute();

        Log.d(TAG, "Authorizing app with server...");

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


    private class SignInTask extends AsyncTask<Void, Void, Boolean> {

        final private String CREDENTIALS_FILE_NAME = "com.unca.android.uncacampusbreeze.credentials";
        final private String CREDENTIALS_FILE_UID_KEY = "uid";

        private FirebaseFunctions functions = FirebaseFunctions.getInstance();

        @Override
        protected void onPreExecute() {
            ProgressBar centerLoadingWheel = findViewById(R.id.serverAuthLoadingWheel);
            centerLoadingWheel.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void ... params) {

            SharedPreferences credentialsSharedPref = getActivity().getSharedPreferences(CREDENTIALS_FILE_NAME, Context.MODE_PRIVATE); // open the file for storing user credentials
            String uid = credentialsSharedPref.getString(CREDENTIALS_FILE_UID_KEY, null);

            if (uid == null) { // device hasn't registered with server before.
//                Toast toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
//                toast.show();

                Log.d(TAG, "Device has never registered with server before.");

                try {
                    String newUid = Tasks.await(createNewAccount());
                    Log.d(TAG, "Awaiting for newUid...");
                    if (newUid == null) { // Something went wrong on server end
                        Log.d(TAG, "newUid is null");
                        return false;
                    } else {
                        SharedPreferences.Editor editor = credentialsSharedPref.edit();
                        editor.putString(CREDENTIALS_FILE_UID_KEY, newUid);
                        uid = newUid;
                        Log.d(TAG, "The new uid is " + uid);
                    }
                } catch (ExecutionException e) {
                    // TODO: Implement something here.
                    return false;
                } catch (InterruptedException e) {
                    // TODO: Implement something here.
                    return false;
                }
            }

            Log.d(TAG, "My uid is " + uid);

            try {
                Log.d(TAG, "Getting token with uid.");
                String token = Tasks.await(getCustomToken(uid));

                if (token == null) {
                    Log.d(TAG, "Token is null.");
                    return false;
                }

                FirebaseAuth auth = FirebaseAuth.getInstance();
                AuthResult authResult =  Tasks.await(auth.signInWithCustomToken(token));

                if (authResult.getUser() == null) {
                    Log.d(TAG, "Could not use token to auth with server.");
                    return false;
                }

            } catch (ExecutionException e) {
                // TODO: Implement something here.
                return false;
            } catch (InterruptedException e) {
                // TODO: Implement something here.
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            ProgressBar centerLoadingWheel = findViewById(R.id.serverAuthLoadingWheel);
            centerLoadingWheel.setVisibility(View.INVISIBLE);

            if (result) {
                Toast toast = Toast.makeText(getActivity(), "Signed in to server.", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getActivity(), "Could not sign in to server.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        private Task<String> createNewAccount() {
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

        private Task<String> getCustomToken(String uid) {
            Map<String, Object> data = new HashMap<>();
            data.put("uid", uid);

            return functions
                    .getHttpsCallable("getCustomToken")
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

}

