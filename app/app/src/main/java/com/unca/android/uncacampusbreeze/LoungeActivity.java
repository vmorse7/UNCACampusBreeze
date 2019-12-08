package com.unca.android.uncacampusbreeze;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.unca.android.uncacampusbreeze.Constants.ERROR_DIALOG_REQUEST;
import static com.unca.android.uncacampusbreeze.Constants.MAPVIEW_BUNDLE_KEY;
import static com.unca.android.uncacampusbreeze.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.unca.android.uncacampusbreeze.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class LoungeActivity extends Activity implements OnMapReadyCallback {

    final private static String TAG = "LoungeActivity";
    final private static String CREDENTIALS_FILE_UID_KEY = "uid";
    final private static String CREDENTIALS_FILE_NAME = "com.unca.android.uncacampusbreeze.credentials";

    private String muid;
    private boolean mLocationGranted = false;
    private MapView mMapView;
    private FusedLocationProviderClient mFusedLocation;//Used to find coordinates
    //private UserLocation mUserLocation; FOR FIREBASE: Create UserLoaction class (see tutorial 7)
    private GoogleMap mGoogleMap;
    private LatLngBounds mMapBoundary;
    private double userLat = 0;
    private double userLong = 0;
    private boolean isLoggedIn = false;

    private GeofencingClient geofencingClient;
    private Geofence geoFence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lounge);
    //    setContentView(R.layout.geofence_layout);
        isLoggedIn = true;
        //mMapView.setVisibility(View.INVISIBLE);
        //Get server tokens: JOHN
        //getLocationPermission();
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        if(isLoggedIn){
            getLocationPermission();
        }
        startGoogleMap(savedInstanceState);
//        Geofence geofence;
//        geofence(30, 35.615709, -82.565609);

        geofencingClient = LocationServices.getGeofencingClient(this);
        String geoID = UUID.randomUUID().toString();

        geoFence = new Geofence.Builder()
                .setRequestId(geoID).setCircularRegion(35.615709, -82.565609, 50)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        return builder.build();
    }


//    public Geofence geofence(float radius, double latitude, double longitude) {
//        String id = UUID.randomUUID().toString();
//        return new Geofence.Builder()
//                .setRequestId(id)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
//                .setCircularRegion(latitude, longitude, radius)
//                .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                .build();
//    }


    public void checkLocation() {
        if (checkMapServices()) {
            if (mLocationGranted) {
                Log.d(TAG, "Location Access has been granted");
                enableButton();
            }
        }
    }

    //Sets button and picture back to invisible so the user has to grant location access again when the app reopens
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        Button button1 = findViewById(R.id.buttonLocate);

        MapView map = findViewById(R.id.mapView);
        Button btn = findViewById(R.id.buttonMessage);
        map.setVisibility(View.INVISIBLE);
        btn.setEnabled(false);


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationPermission();
                checkLocation();
                setCameraView();

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() being called.");

        SharedPreferences credentialsSharedPref = getActivity().getSharedPreferences(CREDENTIALS_FILE_NAME, Context.MODE_PRIVATE);
        String uid = credentialsSharedPref.getString(CREDENTIALS_FILE_UID_KEY, null);
        if (uid == null) { // assume device has never registered with server before.
            registerWithThenSignIntoServer();
        } else {
            signIntoServerWithUid(uid);
        }
        if(isLoggedIn)
        getLastKnownLocation();
        mMapView.onStart();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        //getLocationPermission();
        //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        Log.d(TAG, "LOCATION ACCESS IS " + mLocationGranted);

        if(isLoggedIn){
            if(mLocationGranted){
                map.setMyLocationEnabled(true);
            }
            mGoogleMap = map;
            map.getUiSettings().setAllGesturesEnabled(false);
            setCameraView();
        }

    }

    public void sendToMessages(View view){
        Log.d(TAG, "Send To Messages is called");
        Intent startNewActivity = new Intent(this, FirebasePosts.class);
        startActivity(startNewActivity);

    }


    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
        Log.d(TAG, "onStop() being called.");
    }

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

    //Sets the camera view
    private void setCameraView() {
        if(isLoggedIn){
            getLastKnownLocation();
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


    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart() being called.");
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
                        userLat = location.getLatitude();
                        userLong = location.getLongitude();

                        //run saveduserLoaction once method is created
                    }
                }
            });

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() being called.");


    }

    public Context getActivity() {
        return LoungeActivity.this;
    }

    private void registerWithThenSignIntoServer() {
        Toast toast = Toast.makeText(getActivity(), "Registering for the first time with server...", Toast.LENGTH_SHORT);
        toast.show();

        createNewAccount()
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Toast toast = Toast.makeText(getActivity(), "Registration was a success!", Toast.LENGTH_SHORT);
                        toast.show();
                        getActivity()
                                .getSharedPreferences(CREDENTIALS_FILE_NAME, Context.MODE_PRIVATE)
                                .edit()
                                .putString(CREDENTIALS_FILE_UID_KEY, s)
                                .apply();

                        signIntoServerWithUid(s);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast toast = Toast.makeText(getActivity(), "Registration was a failure!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
    }


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
            getLastKnownLocation();
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
                    getLastKnownLocation();
                }
            }
        }
    }



//Ensure that google services is running and updated
    public boolean isServiceEnabled() {
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

    private void signIntoServerWithUid(String uid) {
        Toast toast = Toast.makeText(getActivity(), "Signing into server...", Toast.LENGTH_SHORT);
        toast.show();

        createCustomTokenForUid(uid)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signInWithCustomToken(s)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast toast = Toast.makeText(getActivity(), "Sign in with server successful.", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast toast = Toast.makeText(getActivity(), "Sign in with server unsuccessful.", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                        Toast toast = Toast.makeText(getActivity(), "Sign in with server unsuccessful.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
    }

    private Task<String> createNewAccount() {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        return functions
                .getHttpsCallable("createNewAccount")
                .call()
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

    private boolean checkMapServices(){
        if(isServiceEnabled()){
            return isMapsEnabled();
        }
        return false;
    }

//This method enables button the messages button and sets the image (to be the google map in the near future) to visible
    public void enableButton() {
        Button btn = findViewById(R.id.buttonMessage);
        MapView map = findViewById(R.id.mapView);
        btn.setEnabled(true);
        map.setVisibility(View.VISIBLE);

    }

    private Task<String> createCustomTokenForUid(String uid) {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", uid);

        FirebaseFunctions functions = FirebaseFunctions.getInstance();

        return functions
                .getHttpsCallable("createCustomTokenForUid")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
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