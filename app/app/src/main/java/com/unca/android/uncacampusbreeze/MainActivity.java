package com.unca.android.uncacampusbreeze;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
}
