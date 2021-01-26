package com.sergei.tracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

class PermissionsClass {

    static class LocationClass {
        static final int locationRequestCode = 1;
        static boolean checkLocationPermissions(Context context) {
            ArrayList<String> list = new ArrayList<>();
            list.add(Manifest.permission.ACCESS_FINE_LOCATION);
            list.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                list.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
            String[] array = list.toArray(new String[0]);

            for (String arrayPoint : array) {
                if(ActivityCompat.checkSelfPermission(context, arrayPoint) != PackageManager.PERMISSION_GRANTED) return false;
            }
            return true;
        }

        static void requestPermissions(Activity activity) {
            ArrayList<String> list = new ArrayList<>();
            list.add(Manifest.permission.ACCESS_FINE_LOCATION);
            list.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                list.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
            String[] array = new String[list.size()];
            ActivityCompat.requestPermissions(activity, list.toArray(array), locationRequestCode);
        }
    }
}
