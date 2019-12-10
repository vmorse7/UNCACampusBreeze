package com.unca.android.uncacampusbreeze;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Source;

import java.util.concurrent.ExecutionException;


public class LocationService extends IntentService {

    private static final String ACTION_START_LOCATION_SERVICE = "com.unca.android.uncacampusbreeze.action.Start_Location_Service";
    private static final String TAG = "LocationService";
    private static final int LOCATION_UPDATE_INTERVAL = 1000;
    private static final int FASTEST_LOCATION_UPDATE_INTERVAL = 500;

    public static enum CampusLocations {
        LIBRARY, LIBRARY_FRONT_DESK, RHODES_ROBINSON;
    }

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private Location mCampusLocation = new Location("");
    private float mMaximumAllowedDistanceFromCampusLocation;
    private Looper mLooper;
    private Location mCurrentLocation;
    private boolean mDeviceIsOnCampus;
    private LocationCallback locationCallback;

    public LocationService() {
        super("LocationService");
    }

    public static void startActionStartLocationService(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(ACTION_START_LOCATION_SERVICE);

        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_LOCATION_SERVICE.equals(action)) {
                handleActionStartLocationService();
            }
        }
    }

    private void handleActionStartLocationService() {
        // setup
        Task<DocumentSnapshot> getGeofenceTask = FirebaseFirestore.getInstance()
                .collection("geofences")
                .document("MAIN_CAMPUS")
                .get(Source.SERVER);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        try {
            DocumentSnapshot ds = Tasks.await(getGeofenceTask);
            GeoPoint geopointFromServer = (GeoPoint) ds.get("center");
            mCampusLocation.setLatitude(geopointFromServer.getLatitude());
            mCampusLocation.setLongitude(geopointFromServer.getLongitude());
            mMaximumAllowedDistanceFromCampusLocation = (float) ds.getLong("radius").floatValue();
        } catch (ExecutionException e) {
            broadcastDeviceIsNotOnCampus();
        } catch (InterruptedException e) {
            broadcastDeviceIsNotOnCampus();
        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d(TAG, "locationResult was null");
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    mCurrentLocation = location;
                }
            }
        };


        mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper());

        while (true) {

            if (mCurrentLocation != null) {

                

                float distanceFromCampusLocation = mCampusLocation.distanceTo(mCurrentLocation);
                if (distanceFromCampusLocation < mMaximumAllowedDistanceFromCampusLocation) {
                    broadcastDeviceIsOnCampus(distanceFromCampusLocation);
                } else {
                    broadcastDeviceIsNotOnCampus(distanceFromCampusLocation);
                }
            }

            SystemClock.sleep(100);
        }
    }

    private void broadcastDeviceLocation() {

    }

    private void broadcastDeviceIsOnCampus(float distance) {
        Intent i = new Intent("on_campus_status");
        i.putExtra("Status", true);
        i.putExtra("Distance", distance);
        getApplicationContext().sendBroadcast(i);
    }

    private void broadcastDeviceIsNotOnCampus() {
        Intent i = new Intent("on_campus_status");
        i.putExtra("Status", false);
        getApplicationContext().sendBroadcast(i);
    }

    private void broadcastDeviceIsNotOnCampus(float distance) {
        Intent i = new Intent("on_campus_status");
        i.putExtra("Status", false);
        i.putExtra("Distance", distance);
        getApplicationContext().sendBroadcast(i);
    }
}
