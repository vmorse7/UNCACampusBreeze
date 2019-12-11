package com.unca.android.uncacampusbreeze;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import static com.unca.android.uncacampusbreeze.Constants.MAPVIEW_BUNDLE_KEY;


public class LoungeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static String TAG = "LoungeActivity";

    private boolean mLocationGranted = false;
    private boolean mLoggedIntoServer = false;
    private boolean mDeviceInValidLocation = false;
    private Context mContext = null;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private LatLngBounds mMapBoundary;

    private Location currentLocation;
    private boolean mapIsReady = false;

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
            startGoogleMap(savedInstanceState);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    9002);
            mLocationGranted = false;
        }




    }

    //Starts google MapView
    private void startGoogleMap(Bundle savedInstanceState){

        //On create portion for MapView according to google's sample code:
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView = findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

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
        if(mapIsReady){
            setCameraView();
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        //getLocationPermission();
        //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        Log.d(TAG, "LOCATION ACCESS IS " + mLocationGranted);


            if(mLocationGranted){
                map.setMyLocationEnabled(true);

            }
            mGoogleMap = map;
            map.getUiSettings().setAllGesturesEnabled(false);




    }
    double userLat;
    double userLong;
    //Sets the camera view NEED LATITUDE AND LONGITUDE OF USER
    private void setCameraView() {



            Log.d(TAG, "USER LATITUDE: " + String.valueOf(userLat));
            Log.d(TAG, "USER LONGITUDE: " + String.valueOf(userLong));
            // mMapView.setVisibility(View.VISIBLE);
            double bottomBoundary = userLat - .1;
            double leftBoundary = userLong - .1;
            double topBoundary = userLat + .1;
            double rightBoundary = userLong + .1;

            mMapBoundary = new LatLngBounds(
                    new LatLng(bottomBoundary, leftBoundary),
                    new LatLng(topBoundary, rightBoundary)
            );


            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary,20));
            mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(17));




    }



        @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();

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
                mapIsReady = true;
                boolean isOnCampus = i.getBooleanExtra("on_campus", false);
                if (isOnCampus) {
                    updateRockyMessage("You are on campus. ");
                    enableButton();
                } else {
                    updateRockyMessage("You are not on campus. ");
                }


            }
            Location l = new Location("");
            l.setLatitude(i.getDoubleExtra("latitude", 50));
            l.setLongitude(i.getDoubleExtra("longitude", 50));
            currentLocation = l;
            userLong = currentLocation.getLongitude();
            userLat = currentLocation.getLatitude();

        }
    };


    private void updateRockyMessage(String message) {

        TextView textView = (TextView) findViewById(R.id.rocky_message);
        textView.setText(message);
    }

}