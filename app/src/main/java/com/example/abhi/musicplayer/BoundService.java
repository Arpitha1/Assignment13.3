package com.example.abhi.musicplayer;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class BoundService extends Service{

    private final IBinder myBinder = new MyLocalBinder();
    private Thread backgroundThread;
    private MediaPlayer player;
    private int NOTIFICATION_ID = 102;

    private String TAG = "bound";


    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind called...");
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i(TAG, "Thread running");
                playMusic();
            }
        });
        backgroundThread.start();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started by startService()");
        return START_STICKY;
    }


    private void  playMusic() {
        if (player != null) {
            player.release();
        }

        player = MediaPlayer.create(this, R.raw.crash_burn);
        player.setLooping(true);
    }


    public void startPlay() {
        if (!player.isPlaying()) {
            player.start();
        }
    }

    //    stop play
    public void stopPlay() {
        if (player.isPlaying()) {
            player.pause();
        }
    }

    public class MyLocalBinder extends Binder {
        BoundService getService() {

            return BoundService.this;
        }
    }


    @Override
    public void onDestroy() {

        Log.i(TAG, "Destroying Service");
        Toast.makeText(this, "Destroying Service...", Toast.LENGTH_SHORT).show();
        player.release();
        player = null;
        Thread dummy = backgroundThread;
        backgroundThread = null;
        dummy.interrupt();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Log.i(TAG, "Cancelling notification");
        notificationManager.cancel(NOTIFICATION_ID);
    }

}
