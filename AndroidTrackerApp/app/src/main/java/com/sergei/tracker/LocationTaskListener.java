package com.sergei.tracker;

import android.content.Context;
import android.location.LocationManager;
import android.os.CountDownTimer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

class LocationTaskListener {
    private final String deviceId;
    private final HttpClient client;
    private final LocationTracker tracker;
    private final ObjectMapper mapper = JacksonConfig.GetConfiguredObjectMapper();
    private final AppConfig appConfig;

    LocationTaskListener(Context context) {
        tracker = new LocationTracker(context);
        client = new HttpClient(context);
        appConfig = new AppConfig(context);
        deviceId = new DeviceRegistration(context).getDeviceId();
    }

    void StartListener() {
        boolean newTaskFromServer = false;
        try {
            newTaskFromServer = GetLocationTask();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (!newTaskFromServer) {
            try {
                RunPreviousTaskAfterReboot();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        new CountDownTimer(AppConfig.Values.TimeBetweenLocationTaskRequests, AppConfig.Values.CountDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                try {
                    GetLocationTask();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                this.start();
            }
        }.start();
    }

    private void RunPreviousTaskAfterReboot() throws JsonProcessingException {
        String previousSessionTask = appConfig.Read(AppConfig.Values.PreviousSessionTaskOptionName, AppConfig.Values.DefaultPreviousSessionTask);
        if (!previousSessionTask.isEmpty()) {
            JsonObjects.TaskJson taskJson = mapper.readValue(previousSessionTask, JsonObjects.TaskJson.class);
            Task task = new Task();
            task.taskCode = taskJson.TaskCode;
            task.minDistance = taskJson.Distance;
            task.minTime = taskJson.Time;
            task.RunTask();
        }
    }

    private boolean GetLocationTask() throws JsonProcessingException {
        client.data = FormJson();
        client.format = HttpClient.MediaTypes.JSON_Format;
        client.url = appConfig.new Server().locationTasksSenderURL;
        try {
            HttpClient.ResponseResult result = client.Send();
            if (result.isSuccessful) {
                String response = result.response;
                if (!response.isEmpty()) {
                    ExecuteTask(response);
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void ExecuteTask(String response) throws JsonProcessingException {
        JsonObjects.TaskJson taskJson = mapper.readValue(response, JsonObjects.TaskJson.class);
        appConfig.Write(AppConfig.Values.PreviousSessionTaskOptionName, response);
        Task task = new Task();
        task.taskCode = taskJson.TaskCode;
        task.minDistance = taskJson.Distance;
        task.minTime = taskJson.Time;
        task.RunTask();
    }

    private class Task {
        int taskCode;
        long minTime;
        int minDistance;

        private void GPS_Once() {
            tracker.locationProvider = LocationManager.GPS_PROVIDER;
            tracker.TrackType = LocationTracker.TrackingType.SINGLE;
        }

        private void GPS_Track() {
            tracker.locationProvider = LocationManager.GPS_PROVIDER;
            tracker.TrackType = LocationTracker.TrackingType.TRACK;
        }

        private void Mobile_Operator_Once() {
            tracker.locationProvider = LocationManager.NETWORK_PROVIDER;
            tracker.TrackType = LocationTracker.TrackingType.SINGLE;
        }

        private void Mobile_Operator_Track() {
            tracker.locationProvider = LocationManager.NETWORK_PROVIDER;
            tracker.TrackType = LocationTracker.TrackingType.TRACK;
        }

        private void Passive_Track() {
            tracker.locationProvider = LocationManager.PASSIVE_PROVIDER;
            tracker.TrackType = LocationTracker.TrackingType.PASSIVE;
        }

        private void GetLastKnownLocation() {
            tracker.TrackType = LocationTracker.TrackingType.LAST_KNOWN;
        }

        private void DisableAllTracking() {
            tracker.TrackType = LocationTracker.TrackingType.DISABLED;
        }

        void RunTask() {
            tracker.MinTimeMs = minTime;
            tracker.MinDistanceMeters = minDistance;
            DisableAllTracking();
            tracker.ExecuteCommand();
            switch (taskCode) {
                case 1:
                    GPS_Once();
                    break;
                case 2:
                    GPS_Track();
                    break;
                case 3:
                    Mobile_Operator_Once();
                    break;
                case 4:
                    Mobile_Operator_Track();
                    break;
                case 5:
                    Passive_Track();
                    break;
                case 6:
                    GetLastKnownLocation();
                    break;
                case 7:
                    DisableAllTracking();
                    break;
            }
            tracker.ExecuteCommand();
        }
    }

    private String FormJson() throws JsonProcessingException {
        JsonObjects.SimpleRequestJson json = new JsonObjects.SimpleRequestJson();
        json.DeviceId = deviceId;
        return mapper.writeValueAsString(json);
    }
}
