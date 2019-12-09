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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Source;


public class LocationService extends IntentService {

    private static final String ACTION_START_LOCATION_SERVICE = "com.unca.android.uncacampusbreeze.action.START_LOCATION_SERVICE";
    private static final String TAG = "LocationService";

    private 

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

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference mainCampusDocRef = db.collection("geofences").document("MAIN_CAMPUS");
        mainCampusDocRef.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Document found in the offline cache
                    DocumentSnapshot document = task.getResult();

                    Log.d(TAG, "Cached document data: " + document.getData());
                } else {

                }
            }
        });

        FusedLocationProviderClient fl = LocationServices.getFusedLocationProviderClient(this);

        while (true) {

            fl.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if(task.isSuccessful()){
                        Location location = task.getResult();
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        Log.d(TAG, "latitude: " + geoPoint.getLatitude());
                        Log.d(TAG, "longitude: " + geoPoint.getLongitude());
//                    userLat = location.getLatitude();
//                    userLong = location.getLongitude();
//                    run saveduserLoaction once method is created
                    }
                }
            });

            SystemClock.sleep(60000);
        }

    }



}
