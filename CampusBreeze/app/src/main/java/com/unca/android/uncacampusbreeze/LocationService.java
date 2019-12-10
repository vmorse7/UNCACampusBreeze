package com.unca.android.uncacampusbreeze;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
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

    private FusedLocationProviderClient mFl;
    private GeoPoint geopointOfMainCampusCenter = null;
    private int radiusOfMainCampus = 0;
//    private GeoPoint geopointOfDevicesCurrentLocation = null;

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

        Task<DocumentSnapshot> getGeofenceTask = FirebaseFirestore.getInstance()
                .collection("geofences")
                .document("MAIN_CAMPUS")
                .get(Source.SERVER);

        Location geofence = new Location("");
        int geofenceRadius = 0;

        try {
            DocumentSnapshot ds = Tasks.await(getGeofenceTask);
            GeoPoint geopointFromServer = (GeoPoint) ds.get("center");
            geofence.setLatitude(geopointFromServer.getLatitude());
            geofence.setLongitude(geopointFromServer.getLongitude());
            geofenceRadius = (int) ds.getLong("radius").intValue();
        } catch (ExecutionException e) {
            Intent i = new Intent("on_campus_status");
            i.putExtra("Status", false);
            getApplicationContext().sendBroadcast(i);
        } catch (InterruptedException e) {
            Intent i = new Intent("on_campus_status");
            i.putExtra("Status", false);
            getApplicationContext().sendBroadcast(i);
        }


        FusedLocationProviderClient mFl = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        long timeOfLastLocationUpdate = System.currentTimeMillis();

        long secondsSinceLastLocationUpdate = 6;
        while (true) {
            if (secondsSinceLastLocationUpdate > 1) {
                Task<Location> getLocationTask = mFl.getLastLocation();

                try {
                    Location lastLocationOfDevice = Tasks.await(getLocationTask);
                    timeOfLastLocationUpdate = System.currentTimeMillis();
                    if (geofence.distanceTo(lastLocationOfDevice) < geofenceRadius) {
                        Intent i = new Intent("on_campus_status");
                        i.putExtra("Status", true);
                        getApplicationContext().sendBroadcast(i);
                    } else {
                        Intent i = new Intent("on_campus_status");
                        i.putExtra("Status", false);
                        getApplicationContext().sendBroadcast(i);
                    }
                } catch (ExecutionException e) {
                    Intent i = new Intent("on_campus_status");
                    i.putExtra("Status", false);
                    getApplicationContext().sendBroadcast(i);
                } catch (InterruptedException e) {
                    Intent i = new Intent("on_campus_status");
                    i.putExtra("Status", false);
                    getApplicationContext().sendBroadcast(i);
                }
            }

            SystemClock.sleep(100);
            secondsSinceLastLocationUpdate = (long) ((System.currentTimeMillis() - timeOfLastLocationUpdate) / 1000);
        }
    }

}
