package com.sergei.tracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.fasterxml.jackson.core.JsonProcessingException;

class LocationTracker {
    enum TrackingType {SINGLE, TRACK, PASSIVE, LAST_KNOWN, DISABLED}

    String locationProvider;
    long MinTimeMs;
    int MinDistanceMeters;
    TrackingType TrackType;
    private final LocationSender locationSender;
    private final LocationListener locationListener;
    private final LocationManager locationManager;

    LocationTracker(Context context) {
        this.locationSender = new LocationSender(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.locationListener = new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { }

            @Override
            public void onProviderDisabled(String provider) { }

            @Override
            public void onProviderEnabled(String provider) { }

            @Override
            public void onLocationChanged(final Location location) {
                if (location != null) {
                    locationSender.latitude = location.getLatitude();
                    locationSender.longitude = location.getLongitude();
                    locationSender.provider = location.getProvider();
                    locationSender.trackingType = TrackType;
                    try {
                        locationSender.Send();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    void ExecuteCommand() {
        if (TrackType == TrackingType.LAST_KNOWN) {
            Location networkLoc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location gpsLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (networkLoc != null) {
                locationSender.latitude = networkLoc.getLatitude();
                locationSender.longitude = networkLoc.getLongitude();
                locationSender.provider = LocationManager.NETWORK_PROVIDER;
                locationSender.trackingType = TrackingType.LAST_KNOWN;
                try {
                    locationSender.Send();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
            else if (gpsLoc != null) {
                locationSender.latitude = gpsLoc.getLatitude();
                locationSender.longitude = gpsLoc.getLongitude();
                locationSender.provider = LocationManager.GPS_PROVIDER;
                locationSender.trackingType = TrackingType.LAST_KNOWN;
                try {
                    locationSender.Send();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }

        else if (TrackType == TrackingType.DISABLED) {
            locationManager.removeUpdates(locationListener);
        }

        else if (locationProvider != null && locationManager.isProviderEnabled(locationProvider)) {

            if (TrackType == TrackingType.TRACK || TrackType == TrackingType.PASSIVE) {
                locationManager.requestLocationUpdates(locationProvider, MinTimeMs, MinDistanceMeters, locationListener);
            }

            else if (TrackType == TrackingType.SINGLE) {
                locationManager.requestSingleUpdate(locationProvider, locationListener, null);
            }
        }
    }
}
