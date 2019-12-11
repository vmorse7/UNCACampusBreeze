package com.unca.android.uncacampusbreeze;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.MapView;


public class LoungeActivity extends AppCompatActivity {

    private static String TAG = "LoungeActivity";

    private boolean mLocationGranted = false;
    private boolean mLoggedIntoServer = false;
    private boolean mDeviceInValidLocation = false;
    private Context mContext = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lounge);
        mContext = getApplicationContext();

        registerReceiver(onLoggedInBroadcast, new IntentFilter("logged_in_status"));
        registerReceiver(onCampusBroadcast, new IntentFilter("location_status"));

        LoginService.startActionLogin(getApplicationContext());

        // ask for location permission if not granted
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) { //Check for ACCESS FINE LOCATION permission
            mLocationGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    9002);
            mLocationGranted = false;
        }

    }

    //This method sends the user to the "Messages" page
    public void sendToMessages(View view){
        Log.d(TAG, "Send To Messages is called");
        Intent startNewActivity = new Intent(this, FirebasePosts.class);
        startActivity(startNewActivity);

    }

    //This method enables button the messages button and sets the image (to be the google map in the near future) to visible
    public void enableButton() {
        Button btn = findViewById(R.id.buttonMessage);
        MapView map = findViewById(R.id.mapView);
        btn.setEnabled(true);
        map.setVisibility(View.VISIBLE);
    }



        @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onRestart() {
        super.onRestart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(onLoggedInBroadcast);
        unregisterReceiver(onCampusBroadcast);
    }

    private BroadcastReceiver onLoggedInBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {
            boolean isLoggedIn = i.getBooleanExtra("Status", false);
            if (isLoggedIn) {
                mLoggedIntoServer = true;
                LocationService.startActionStartLocationService(getApplicationContext());
                updateRockyMessage("Logged into server.");
            } else {
                mLoggedIntoServer = false;
                updateRockyMessage("Could not log into server.");
            }
        }
    };

    private BroadcastReceiver onCampusBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {

            boolean isError = i.getBooleanExtra("error", true);
            if (isError) {
                updateRockyMessage("Error in getting location.");
            } else {
                boolean isOnCampus = i.getBooleanExtra("on_campus", false);
                if (isOnCampus) {
                    updateRockyMessage("You are on campus. ");
                    enableButton();
                } else {
                    updateRockyMessage("You are not on campus. ");
                }
            }
        }
    };

    private void updateRockyMessage(String message) {
        TextView textView = (TextView) findViewById(R.id.rocky_message);
        textView.setText(message);
    }

}