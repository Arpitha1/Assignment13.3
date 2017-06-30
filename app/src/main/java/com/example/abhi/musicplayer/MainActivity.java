package com.example.abhi.musicplayer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    private BoundService serviceReference;
    private int REQUEST_CODE = 101;
    private int NOTIFICATION_ID = 102;
    private boolean isBound;
    private String TAG = "bound";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "in MainActivity onCreate");


        Log.i(TAG, "Service starting...");
        Intent intent = new Intent(this, BoundService.class);
        startService(intent);
        sendNotification();


        Button startButton = (Button) findViewById(R.id.buttonStart);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound) {
                    serviceReference.startPlay();
                }
            }
        });


        Button stopButton = (Button) findViewById(R.id.buttonStop);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound) {
                    serviceReference.stopPlay();
                }
            }
        });
    }

    private ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Bound service connected");
            serviceReference = ((BoundService.MyLocalBinder) service).getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Problem: bound service disconnected");
            serviceReference = null;
            isBound = false;
        }
    };


    private void doUnbindService() {
        Toast.makeText(this, "Unbinding...", Toast.LENGTH_SHORT).show();
        unbindService(myConnection);
        isBound = false;
    }


    private void doBindToService() {
        Toast.makeText(this, "Binding...", Toast.LENGTH_SHORT).show();
        if (!isBound) {
            Intent bindIntent = new Intent(this, BoundService.class);
            isBound = bindService(bindIntent, myConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "MainActivity - onStart - binding...");
        doBindToService();
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "MainActivity - onStop - unbinding...");
        doUnbindService();
    }

    @Override
    public void onBackPressed() {

        moveTaskToBack(true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Destroying activity...");

        if (isFinishing()) {
            Log.i(TAG, "activity is finishing");
            Intent intentStopService = new Intent(this, BoundService.class);
            stopService(intentStopService);
        }
    }


    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Service Running")
                .setTicker("Music Playing")
                .setWhen(System.currentTimeMillis())
                .setOngoing(true);
        Intent startIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                REQUEST_CODE, startIntent, 0);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}



