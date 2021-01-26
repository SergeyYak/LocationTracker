package com.sergei.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Patterns;

import java.time.Duration;


class AppConfig {
    final Context context;

    AppConfig(Context context) {
        this.context = context;
    }

    class Server {
        final String serverHost = Read(Values.ServerHostOptionName, Values.DefaultServerHost);
        final String locationReceiverURL = serverHost + "/LocationReceiver";
        final String registerDeviceURL = serverHost + "/RegisterDevice";
        final String locationTasksSenderURL = serverHost + "/TaskSender";
        final Duration timeout = Duration.ofSeconds(5);
    }

    static class Values {
        static final String EmptyString = "";

        static final int CountDownInterval = 1000;
        static final int TimeBetweenLocationTaskRequests = 60 * 1000;

        static final String DeviceIdOptionName = "DeviceId";
        static final String DefaultDeviceId = "-1";

        static final String ServerHostOptionName = "ServerHost";
        static final String DefaultServerHost = "https://";

        static final String LoginOptionName = "Login";
        static final String DefaultLogin = "0000";

        static final String PreviousSessionTaskOptionName = "PreviousSessionTask";
        static final String DefaultPreviousSessionTask = EmptyString;
    }

    void Write(String key, String value) {
        SharedPreferences myPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor myEditor = myPreferences.edit();
        myEditor.putString(key, value);
        myEditor.apply();
    }

    String Read(String key, String defValue) {
        SharedPreferences myPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);
        return myPreferences.getString(key, defValue);
    }

    boolean ValidateURL(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    boolean[] CheckIfAppReadyForLaunch() {
        boolean[] ready = new boolean[3];
        ready[0] = !(new DeviceRegistration(context).getDeviceId().equals(Values.DefaultDeviceId));
        ready[1] = ValidateURL(new Server().serverHost);
        ready[2] = PermissionsClass.LocationClass.checkLocationPermissions(context);
        return ready;
    }
}
