package com.example.sean.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.sean.myapplication.MESSAGE";

    private boolean beepOn = false;
    private boolean fast = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {

        // Do something in response to button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }


    public void startBeep(View view) {
        if (beepOn) {
            return;
        }

        beepOn = true;
        Thread mThread = new Thread (new Runnable() {
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);


                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.beep5);

                while (beepOn) {

                    mp.start();
                    try {
                        int timeToSleep = 1000;
                        if (fast) {
                            timeToSleep = 100;
                        }
                        Thread.sleep(timeToSleep);
                    } catch (InterruptedException e) {
                    }
                }
            }
        });

        mThread.start();
    }

    public void stopBeep(View view) {
        beepOn = false;
    }

    public void slow(View view) {
        fast = false;
    }

    public void fast(View view) {
        fast = true;
    }

    public void setVolumeHigh(View view) {
        AudioManager audioManager =
                (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                50, 0);
    }

    public void setVolumeLow(View view) {
        AudioManager audioManager =
                (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                5, 0);
    }
}
