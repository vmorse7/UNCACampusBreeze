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

        final private String TAG = "SignInTask";
        final private String CREDENTIALS_FILE_UID_KEY = "uid";
        final private String CREDENTIALS_FILE_NAME = "com.unca.android.uncacampusbreeze.credentials";

        private FirebaseFunctions functions = FirebaseFunctions.getInstance();
        private SharedPreferences credentialsSharedPref;
        private String uid;

        @Override
        protected void onPreExecute() {
            // set the gui to desired state
            ProgressBar centerLoadingWheel = findViewById(R.id.serverAuthLoadingWheel);
            centerLoadingWheel.setVisibility(View.VISIBLE);

            credentialsSharedPref = getActivity().getSharedPreferences(CREDENTIALS_FILE_NAME, Context.MODE_PRIVATE); // open the file for storing user credentials
            uid = credentialsSharedPref.getString(CREDENTIALS_FILE_UID_KEY, null); // get the uid stored on device, if dne then set  it to null
        }

        @Override
        protected Boolean doInBackground(Void ... params) {
            Log.d(TAG, "doInBackground() is running.");

            if (uid == null) { // device hasn't registered with server before.
                Log.d(TAG, "uid == null");

                createNewAccount()
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                uid = s;
                                SharedPreferences.Editor editor = credentialsSharedPref.edit();
                                editor.putString(CREDENTIALS_FILE_UID_KEY, uid);
                                Log.d(TAG, "uid is now " + uid);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, e.toString());
                            }
                        });

//
//            try {
//                Log.d(TAG, "Getting token with uid.");
//                String token = Tasks.await(getCustomToken(uid));
//
//                if (token == null) {
//                    Log.d(TAG, "Token is null.");
//                    return false;
//                }
//
//                FirebaseAuth auth = FirebaseAuth.getInstance();
//                AuthResult authResult =  Tasks.await(auth.signInWithCustomToken(token));
//
//                if (authResult.getUser() == null) {
//                    Log.d(TAG, "Could not use token to auth with server.");
//                    return false;
//                }
//
//            } catch (ExecutionException e) {
//                // TODO: Implement something here.
//                return false;
//            } catch (InterruptedException e) {
//                // TODO: Implement something here.
//                return false;
//            }

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

