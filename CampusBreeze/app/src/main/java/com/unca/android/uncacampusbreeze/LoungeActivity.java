package com.unca.android.uncacampusbreeze;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


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
        registerReceiver(onCampusBroadcast, new IntentFilter("on_campus_status"));

        LoginService.startActionLogin(getApplicationContext());

        // ask for location permission if not granted
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) { //Check for ACCESS FINE LOCATION permission
            mLocationGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    9002);
            mLocationGranted = false;
        }

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
            boolean isOnCampus = i.getBooleanExtra("Status", false);
            if (isOnCampus) {
                updateRockyMessage("You are on campus.");
            } else {
                updateRockyMessage("You are not on campus.");
            }
        }
    };

    private void updateRockyMessage(String message) {
        TextView textView = (TextView) findViewById(R.id.rocky_message);
        textView.setText(message);
    }

}
