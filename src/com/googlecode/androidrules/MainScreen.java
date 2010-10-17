package com.googlecode.androidrules;

import com.googlecode.androidrules.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainScreen extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button startStopButton = (Button) findViewById(R.id.StartStop);
        startStopButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(".androidrules.START_STOP_SERVICE");
                    if (!BroadcastsHandlerService.isRunning()) {
                        startService(intent);
                        Toast.makeText(getApplicationContext(), "Android Rules started", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        stopService(intent);
                        Toast.makeText(getApplicationContext(), "Android Rules stopped", Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }
}