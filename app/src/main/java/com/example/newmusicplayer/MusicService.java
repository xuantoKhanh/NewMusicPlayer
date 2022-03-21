package com.example.newmusicplayer;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MusicService extends Service {
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PREV = "PREVIOUS";
    public static final String ACTION_PLAY = "PLAY";
    ActionPlaying actionPlaying;
    private IBinder mBinder = new MyBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Method");
        return mBinder;
    }
    public class MyBinder extends Binder {
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String actionName = intent.getStringExtra("myActionName");
        if(actionName != null){
            switch (actionName){
                case ACTION_PLAY:
                    if (actionPlaying != null) {

                        actionPlaying.playClicked();
                    }
                    break;
                case ACTION_PREV:
                    if (actionPlaying != null) {

                        actionPlaying.prevClicked();
                    }
                    break;
                case ACTION_NEXT:
                    if (actionPlaying != null) {

                        actionPlaying.nextClicked();
                    }
                    break;

            }
        }


        return START_STICKY;
    }
    public void setCallBack(ActionPlaying actionPlaying){
        this.actionPlaying = actionPlaying;

    }
}
