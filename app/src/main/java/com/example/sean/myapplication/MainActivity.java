package com.example.sean.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.sean.myapplication.MESSAGE";

    private boolean beepOn = false;
    private boolean fast = false;

    TextView timerTextView;
    long startTime = 0;

    MediaRecorder mediaRecorder = null;
    boolean isPermissionGranted = false;
    public static final int RequestPermissionCode = 1;
    String message = "";

    int ambientVolume = 0;
    int silencesToRepeat = 2;

    //runs without a timer by reposting this handler at the end of the runnable
//    Handler timerHandler = new Handler();
//    Runnable timerRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            long millis = System.currentTimeMillis() - startTime;
//            int seconds = (int) (millis / 1000);
//            int minutes = seconds / 60;
//            seconds = seconds % 60;
//
//            timerTextView.setText(String.format("%d:%02d", minutes, seconds));
//
//            // check volume, set output...
//
//            TextView volumeLevel = (TextView) findViewById(R.id.textView3);
//
//            ambientVolume = getAmbientVolume();
//            setOutputVolume();
//            volumeLevel.setText("mediaRecorder is: " + message + "\nambientVolume: " + ambientVolume);
//
//            // close all recording abjects, to purge memory...
////            stopRecording();
//
//
//            // start recording
//            startRecording();
//
//            timerHandler.postDelayed(this, 500);
//        }
//    };

    private void setOutputVolume() {
        int volume = getOutputVolume();
        AudioManager audioManager =
                (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                volume, 0);
    }

    private int getOutputVolume() {
        int level;
        if (ambientVolume < 2000) {
            level = 6;
        } else if (ambientVolume < 4000) {
            level = 7;
        } else if (ambientVolume < 6000) {
            level = 8;
        } else if (ambientVolume < 7000) {
            level = 9;
        } else if (ambientVolume < 8000) {
            level = 10;
        } else if (ambientVolume < 9000) {
            level = 11;
        } else if (ambientVolume < 10000) {
            level = 12;
        } else if (ambientVolume < 11000) {
            level = 13;
        } else if (ambientVolume < 14000) {
            level = 14;
        } else {
            level = 15;
        }
        return level;
    }

    private void startRecording() {
        getMicrophonePermission();
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile("/dev/null");
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getMicrophonePermission() {
        if (!isPermissionGranted) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO}, RequestPermissionCode);
            isPermissionGranted = checkPermission();
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean RecordPermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this,"Permission Denied",
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private int getAmbientVolume() {
        if (mediaRecorder == null) {
            message = "null";
            return 0;
        } else {
            message = "not null";
            return mediaRecorder.getMaxAmplitude();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerTextView = (TextView) findViewById(R.id.textView2);
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


                final MediaPlayer click = MediaPlayer.create(getApplicationContext(), R.raw.click100msloud);
                final MediaPlayer silence = MediaPlayer.create(getApplicationContext(), R.raw.silence100ms);
                final int[] numSilences = {silencesToRepeat};

                click.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        silence.start();
                    }
                });

                silence.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {

                        TextView volumeLevel = (TextView) findViewById(R.id.textView3);

                        if (numSilences[0] > 0) {
                            if (numSilences[0] == 2) {
                                ambientVolume = getAmbientVolume();
                            }
                            numSilences[0] = numSilences[0] - 1;
                            silence.start();
                        } else {
                            numSilences[0] = silencesToRepeat;
                            if (beepOn) {
                                ambientVolume = getAmbientVolume();
                                setOutputVolume();
                                volumeLevel.setText("\nmediaRecorder is: " + message + "\nambientVolume: " + ambientVolume);
//                                ambientVolume = 0;
                                click.start();
                            }
                        }

                    }
                });

                click.start();
                startRecording();
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
                12, 0);
    }

    public void setVolumeLow(View view) {
        AudioManager audioManager =
                (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                4, 0);
    }
}
