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
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import static com.unca.android.uncacampusbreeze.Constants.ERROR_DIALOG_REQUEST;
import static com.unca.android.uncacampusbreeze.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.unca.android.uncacampusbreeze.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends AppCompatActivity {


    private boolean mLocationGranted = false;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocationPermission();

    }

    //Sets button and picutre back to invisible so the user has to grant location access again when the app reopens
    @Override
    public void onResume(){
        super.onResume();
        Button button1 = findViewById(R.id.buttonLocate);

        ImageView img = findViewById(R.id.imageView);
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

    }




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

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //play service is working and user can make map request
            Log.d(TAG, "Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
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
        ImageView img = findViewById(R.id.imageView);
        btn.setEnabled(true);
        img.setVisibility(View.VISIBLE);

    }





}
