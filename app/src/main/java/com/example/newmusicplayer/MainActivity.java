package com.example.newmusicplayer;


import static com.example.newmusicplayer.ApplicationClass.ACTION_NEXT;
import static com.example.newmusicplayer.ApplicationClass.ACTION_PLAY;
import static com.example.newmusicplayer.ApplicationClass.ACTION_PREV;
import static com.example.newmusicplayer.ApplicationClass.CHANNEL_ID_1;
import static com.example.newmusicplayer.ApplicationClass.CHANNEL_ID_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.session.MediaSessionCompat;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.channels.spi.AbstractSelectionKey;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActionPlaying, ServiceConnection {


    ArrayList<TrackFiles> trackFilesArrayList = new ArrayList<>();
    ImageView next, prev, play;
    TextView title;
    int position = 0;
    boolean isPlaying = false;
    MusicService musicService;
    MediaSessionCompat mediaSession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        next = findViewById(R.id.next);
        prev = findViewById(R.id.previous);
        play = findViewById(R.id.play);
        title = findViewById(R.id.titleSongs);
        Log.e("Playing", isPlaying + " ");
        mediaSession = new MediaSessionCompat(this, "PlayerAudio");


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextClicked();
                Log.e("Playing", isPlaying + " ");

            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevClicked();
                Log.e("Playing", isPlaying + " ");

            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playClicked();
                Log.e("Playing", isPlaying + " ");

            }
        });

        populateFiles();
        title.setText(trackFilesArrayList.get(position).getTitle());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void populateFiles(){
        TrackFiles trackFiles = new TrackFiles("Faded",
                "Alan Walker", R.drawable.fade);
        trackFilesArrayList.add(trackFiles);
        TrackFiles trackFiles1 = new TrackFiles("Attention",
                "Charlie Puth", R.drawable.attention);
        trackFilesArrayList.add(trackFiles1);
        TrackFiles trackFiles2 = new TrackFiles("Baarish",
                "Darshan Raval", R.drawable.baarish);
        trackFilesArrayList.add(trackFiles2);
        TrackFiles trackFiles3 = new TrackFiles("Believer",
                "Image Dragons", R.drawable.believer);
        trackFilesArrayList.add(trackFiles3);

    }

    @Override
    public void nextClicked() {
        if(position ==3){
            position = 0;
        }else{
            position++;
        }
        title.setText(trackFilesArrayList.get(position).getTitle());
        if(!isPlaying){
            showNotification(R.drawable.ic_pause);
        }else{

            showNotification(R.drawable.ic_play);
        }

    }

    @Override
    public void prevClicked() {
        if(position ==0){
            position = 3;
        }else{
            position--;
        }
        title.setText(trackFilesArrayList.get(position).getTitle());
        if(!isPlaying){

            showNotification(R.drawable.ic_pause);
        }else{

            showNotification(R.drawable.ic_play);
        }

    }

    @Override
    public void playClicked() {
        if(!isPlaying){
            isPlaying   = true;
            play.setImageResource(R.drawable.ic_pause);
            showNotification(R.drawable.ic_pause);
        }else{
            isPlaying   = false;
            play.setImageResource(R.drawable.ic_play);
            showNotification(R.drawable.ic_play);
        }

    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicService.MyBinder binder = (MusicService.MyBinder) iBinder;
        musicService = binder.getService();
        musicService.setCallBack(MainActivity.this);
        Log.e("Connected", musicService + "");

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
        Log.e("Disconnected", musicService + "");
    }
    public void showNotification(int playPauseBtn){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, intent, 0);
        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0,
                prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0,
                nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent playIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0,
                playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap picture = BitmapFactory.decodeResource(getResources(),
                trackFilesArrayList.get(position).getThumbnail());

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                .setSmallIcon(trackFilesArrayList.get(position).getThumbnail())
                .setLargeIcon(picture)
                .setContentTitle(trackFilesArrayList.get(position).getTitle())
                .setContentText(trackFilesArrayList.get(position).getSinger())
                .addAction(R.drawable.ic_previous, "Previous", prevPendingIntent)
                .addAction(playPauseBtn, "Play", playPendingIntent)
                .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
//                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                        .setMediaSession(mediaSession.getSessionToken()))

                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .build();


        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}

