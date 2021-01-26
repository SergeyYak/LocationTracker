package com.sergei.tracker;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

class LocationSender {
    double latitude;
    double longitude;
    String provider;
    LocationTracker.TrackingType trackingType;
    private final Context context;
    private final String deviceId;
    private final ObjectMapper mapper = JacksonConfig.GetConfiguredObjectMapper();

    LocationSender(Context context) {
        this.context = context;
        deviceId = new DeviceRegistration(context).getDeviceId();
    }

    void Send() throws JsonProcessingException {

        LocalDateTime currentTime = LocalDateTime.now();
        JsonObjects.LocationJson locationJson = new JsonObjects.LocationJson();
        locationJson.DeviceId = deviceId;
        locationJson.Latitude = latitude;
        locationJson.Longitude = longitude;
        locationJson.Provider = provider;
        locationJson.TrackingType = trackingType.toString().toLowerCase();
        locationJson.LocationUpdatedTime = currentTime;

        String jsonString = mapper.writeValueAsString(locationJson);

        HttpClient client = new HttpClient(context);
        client.data = jsonString;
        client.format = HttpClient.MediaTypes.JSON_Format;
        client.url = new AppConfig(context).new Server().locationReceiverURL;
        try {
            client.Send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
