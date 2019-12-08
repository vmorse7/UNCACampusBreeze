package com.unca.android.uncacampusbreeze;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class LoungeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lounge);

        // We start the gatekeeping background service
    }

    @Override
    public void onStart() {
        super.onStart();

//        startService(new Intent(context, WifiSearchService.class));
//        startService(new Intent(getApplicationContext(), GatekeepingService.class));
        GatekeepingService.startActionGatekeep(getApplicationContext());
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onRestart() {
        super.onRestart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
