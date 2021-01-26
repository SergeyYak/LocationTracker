package com.sergei.tracker;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

class DeviceRegistration {
    static final String deviceName = getDeviceName();
    private final HttpClient client;
    private final Context context;
    private final AppConfig appConfig;

    DeviceRegistration(Context context) {
        this.context = context;
        client = new HttpClient(context);
        appConfig = new AppConfig(context);
    }

    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;

    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }
        return phrase.toString();
    }

    String getDeviceId() {
        return new AppConfig(context).Read(AppConfig.Values.DeviceIdOptionName, AppConfig.Values.DefaultDeviceId);
    }

    String RegisterDevice() {
        String deviceId = getDeviceId();
        JSONObject userObj = new JSONObject();
        try {
            userObj.put("DeviceName", deviceName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        client.data = userObj.toString();
        client.format = HttpClient.MediaTypes.JSON_Format;
        client.url = appConfig.new Server().registerDeviceURL;
        HttpClient.ResponseResult result;
        try {
            result = client.Send();
            if (result.isSuccessful) {
                deviceId = result.response;
                appConfig.Write(AppConfig.Values.DeviceIdOptionName, deviceId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deviceId;
    }
}
