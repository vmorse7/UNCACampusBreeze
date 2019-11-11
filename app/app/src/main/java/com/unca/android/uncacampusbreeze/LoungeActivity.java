package com.unca.android.uncacampusbreeze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import static com.unca.android.uncacampusbreeze.Constants.ERROR_DIALOG_REQUEST;
import static com.unca.android.uncacampusbreeze.Constants.MAPVIEW_BUNDLE_KEY;
import static com.unca.android.uncacampusbreeze.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.unca.android.uncacampusbreeze.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class LoungeActivity extends AppCompatActivity implements OnMapReadyCallback {//Adding implements OnMapReady CallBack


    private boolean mLocationGranted = false;
    private static final String TAG = "LoungeActivity";
    private MapView mMapView;
    private FusedLocationProviderClient mFusedLocation;//Used to find coordinates
    //private UserLocation mUserLocation; FOR FIREBASE: Create UserLoaction class (see tutorial 7)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lounge);

        //Get server tokens: JOHN

        //getLocationPermission();

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);

        startGoogleMap(savedInstanceState);
        getLastKnownLocation();


    }


    //Sets button and picture back to invisible so the user has to grant location access again when the app reopens
    @Override
    public void onResume() {
        super.onResume();
        Button button1 = findViewById(R.id.buttonLocate);

        MapView img = findViewById(R.id.mapView);
        Button btn = findViewById(R.id.buttonMessage);
        img.setVisibility(View.INVISIBLE);
        btn.setEnabled(false);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationPermission();
                checkLocation();
            }
        });
        mMapView.onResume();
    }


    @Override
    public void onStart() {
        super.onStart();

        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        //getLocationPermission();
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        Log.d(TAG, "LOCATION ACCESS IS " + mLocationGranted);

        if(mLocationGranted){
            map.setMyLocationEnabled(true);

        }


    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    //mapView stuff


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







    //CREATE saveUserLoaction() public void See tutorial 7


    //Find last known location of user
    private void getLastKnownLocation(){
        if(mLocationGranted){
            Log.d(TAG, "LAST KNOWN LOCATION IS CALLED");
            mFusedLocation.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if(task.isSuccessful()){
                        Location location = task.getResult();
                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                        Log.d(TAG, "latitude: " + geoPoint.getLatitude());
                        Log.d(TAG, "longitude: " + geoPoint.getLongitude());

                        //run saveduserLoaction once method is created
                    }
                }
            });

        }
    }


    //Sets button and picture back to invisible so the user has to grant location access again when the app reopens
    //7:22




    public void sendToMessages(View view){
        Intent startNewActivity = new Intent(this, PostListActivity.class);
        startActivity(startNewActivity);

    }



    public void checkLocation() {
        if (checkMapServices()) {
            if (mLocationGranted) {
                Log.d(TAG, "Location Access has been granted");
                enableButton();
            }
        }
    }








    //Used CodingWithMitch's tutorial as a guide for google maps integration (https://www.youtube.com/watch?v=118wylgD_ig&list=PLgCYzUzKIBE-SZUrVOsbYMzH7tPigT3gi&index=5)



    //This creates an Alert if the user does not have GPS function on, and directs them to the settings page which allows the user to enable GPS
    private void noGpsAllert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work, you need to enable it...")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent goToGPS = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(goToGPS, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }






    //This method checks to see if the GPS is setting is enabled
    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            noGpsAllert();
            return false;
        }
        return true;
    }



    //This method requests the location permission. In order to eventually get the location of the device.
    //The result will be handled in another method (onRequestPermissionsResult())
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {//Check for ACCESS FINE LOCATION permission
            Log.d(TAG, "location accessed");
            mLocationGranted = true;

            checkLocation();
            //getLastKnownLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            mLocationGranted = false;
            Log.d(TAG, "location denied");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationGranted = true;
                }
            }
        }
    }



//Ensure that google services is running and updated
    public boolean isServiceEnabled(){
        Log.d(TAG, "checking google services version!");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LoungeActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //play service is working and user can make map request
            Log.d(TAG, "Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoungeActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            //user can't make map requests becuase of google play services, we did everything we could
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationGranted){
                   enableButton();//if mLocationGranted is true enable the button
                   getLastKnownLocation();//MIGHT BE CAUSING ISSUES
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }


    private boolean checkMapServices(){
        if(isServiceEnabled()){
            return isMapsEnabled();
        }
        return false;
    }
//This method enables button the messages button and sets the image (to be the google map in the near future) to visible
    public void enableButton(){
        Button btn = findViewById(R.id.buttonMessage);
        MapView img = findViewById(R.id.mapView);
        btn.setEnabled(true);
        img.setVisibility(View.VISIBLE);

    }

    //This is where we implement the google mapView


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }


}

