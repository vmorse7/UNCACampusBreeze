package com.unca.android.uncacampusbreeze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

public class LoungeActivity extends AppCompatActivity {

    private boolean mLocationGranted = false;
    private Intent mGatekeepingStatusIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lounge);

        // ask for location permission if not granted
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {//Check for ACCESS FINE LOCATION permission
//            Log.d(TAG, "location accessed");
            mLocationGranted = true;

//            checkLocation();
//            getLastKnownLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    9002);
            mLocationGranted = false;
//            Log.d(TAG, "location denied");
        }

        mGatekeepingStatusIntent = new Intent(LoungeActivity.this, GatekeepingService.class);
        mGatekeepingStatusIntent.putExtra("LOUNGE_IS_LOCKED", true);
        GatekeepingService.startActionGatekeep(getApplicationContext());        // We start the gatekeeping background servic
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(onBroadcast, new IntentFilter("unlock_lounge"));

    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(gatekeepBroadcast);
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

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationGranted = false;
        switch (requestCode) {
            case 9002: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationGranted = true;
//                    getLastKnownLocation();
                }
            }
        }
    }

//    private void getLastKnownLocation(){
//        if(mLocationGranted){
//            Log.d(TAG, "LAST KNOWN LOCATION IS CALLED");
//            mFusedLocation.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//                @Override
//                public void onComplete(@NonNull Task<Location> task) {
//                    if(task.isSuccessful()){
//                        Location location = task.getResult();
//                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
//                        Log.d(TAG, "latitude: " + geoPoint.getLatitude());
//                        Log.d(TAG, "longitude: " + geoPoint.getLongitude());
//                        userLat = location.getLatitude();
//                        userLong = location.getLongitude();
//
//                        //run saveduserLoaction once method is created
//                    }
//                }
//            });
//
//        }
//    }

    private BroadcastReceiver gatekeepBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {

        }
    };
}
