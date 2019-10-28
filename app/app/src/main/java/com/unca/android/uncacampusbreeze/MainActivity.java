package com.unca.android.uncacampusbreeze;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }





    public void sendToMessages(View view){
        //Intent startNewActivity = new Intent(this, nextScreen.class); IMPORTANT: replace nextScreen.class with main class
        //startActivity(startNewActivity);
    }


    //This method check to see if the GPS is setting is enabled
    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {

            return false;
        }
        return true;
    }






}
