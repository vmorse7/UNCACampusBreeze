package com.unca.android.uncacampusbreeze;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class LocationService extends IntentService {

    private static final String ACTION_START_LOCATION_SERVICE = "com.unca.android.uncacampusbreeze.action.Start_Location_Service";
    private static final String TAG = "LocationService";
    private static final int LOCATION_UPDATE_INTERVAL = 10000;
    private static final int FASTEST_LOCATION_UPDATE_INTERVAL = 5000;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    private Location mCurrentLocation;

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
        Task<QuerySnapshot> getMapPolygonsTask = FirebaseFirestore.getInstance().collection("map_polygons").get();
        try {
            MapPolygons mp = MapPolygons.getInstance();
            QuerySnapshot qs = Tasks.await(getMapPolygonsTask);
            for (QueryDocumentSnapshot document : qs) {
                Polygon polygon = new Polygon;
                List<LatLng> points = (List<LatLng>) ocument.get("points");
                polygon.setPoints(points);
                mp.insertPolygonInMap(document.getString("nameOfPlace"), );

            }
        } catch (ExecutionException e) {

        } catch (InterruptedException e) {

        }


//        Task<DocumentSnapshot> getGeofenceTask = FirebaseFirestore.getInstance()
//                .collection("geofences")
//                .document("johns_house")
//                .get(Source.SERVER);

        Location campusCenter = new Location("");
        float campusRadius = Long.MAX_VALUE;
        try {
            DocumentSnapshot ds = Tasks.await(getGeofenceTask);
            GeoPoint geopointOfCampusCenter = (GeoPoint) ds.get("center");
            campusCenter.setLatitude(geopointOfCampusCenter.getLatitude());
            campusCenter.setLongitude(geopointOfCampusCenter.getLongitude());
            campusRadius = (float) ds.getLong("radius").floatValue();
        } catch (ExecutionException e) {
            Intent i = new Intent("location_status");
            i.putExtra("error", true);
            i.putExtra("on_campus", false);
            getApplicationContext().sendBroadcast(i);
        } catch (InterruptedException e) {
            Intent i = new Intent("location_status");
            i.putExtra("error", true);
            i.putExtra("on_campus", false);
            getApplicationContext().sendBroadcast(i);
        }

        setupLocationFinding();

        while (true) {
            if (mCurrentLocation != null) {
                float distanceFromCampus = campusCenter.distanceTo(mCurrentLocation);
                if (distanceFromCampus < campusRadius) {
                    Intent i = new Intent("location_status");
                    i.putExtra("error", false);
                    i.putExtra("on_campus", true);
                    i.putExtra("latitude",mCurrentLocation.getLatitude());
                    i.putExtra("longitude",mCurrentLocation.getLongitude());
                    getApplicationContext().sendBroadcast(i);

                } else {
                    Intent i = new Intent("location_status");
                    i.putExtra("error", false);
                    i.putExtra("on_campus", false);
                    i.putExtra("latitude",mCurrentLocation.getLatitude());
                    i.putExtra("longitude",mCurrentLocation.getLongitude());
                    getApplicationContext().sendBroadcast(i);

                }
            } else {
                Intent i = new Intent("location_status");
                i.putExtra("error", true);
                i.putExtra("on_campus", false);
                getApplicationContext().sendBroadcast(i);
            }

            SystemClock.sleep(100);
        }
    }

    private void setupLocationFinding() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    mCurrentLocation = location;
                }
            }
        };

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper());
    }
}