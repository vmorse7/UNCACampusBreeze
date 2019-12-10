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
        try {
            DocumentSnapshot ds = Tasks.await(getGeofenceTask);
            geopointOfMainCampusCenter = (GeoPoint) ds.get("center");
            radiusOfMainCampus = (int) ds.getLong("radius").intValue();
        } catch (ExecutionException e) {

        } catch (InterruptedException e) {

        }


        mFl = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        while (true) {
            Log.d(TAG, "At beginning of loop in LocationServiceLoop().");
            mFl.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Location locationOfMainCampus = new Location("");
                            locationOfMainCampus.setLatitude(geopointOfMainCampusCenter.getLatitude());
                            locationOfMainCampus.setLongitude(geopointOfMainCampusCenter.getLongitude());

                            if (locationOfMainCampus.distanceTo(location) > (float) radiusOfMainCampus) {
                                Log.d(TAG, "YOU ARE NOT ON CAMPUS");
                                Intent i = new Intent("on_campus_status");
                                i.putExtra("Status", false);
                                getApplicationContext().sendBroadcast(i);
                            } else {
                                Intent i = new Intent("on_campus_status");
                                Log.d(TAG, "YOU ARE ON CAMPUS");
                                i.putExtra("Status", true);
                                getApplicationContext().sendBroadcast(i);
                            }

                        }
                    });

            SystemClock.sleep(5000);
        }
    }

//        FirebaseFirestore
//                .getInstance()
//                .collection("geofences")
//                .document("MAIN_CAMPUS")
//                .get(Source.SERVER)
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if (documentSnapshot.exists()) {
//                            geopointOfMainCampusCenter = (GeoPoint) documentSnapshot.get("center");
//                            radiusOfMainCampus = (int) documentSnapshot.getLong("radius").intValue();
//                            runLocationServiceLoop();
//                        }
//                    }
//                });
//
//        while (true) {  // I DK if this is needed, but seems reasonable.
//            SystemClock.sleep(1000);
//        }


}
