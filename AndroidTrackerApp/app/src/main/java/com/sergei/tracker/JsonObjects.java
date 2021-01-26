package com.sergei.tracker;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class JsonObjects {
    public static class TaskJson{
        public int TaskCode;
        public long Time;
        public int Distance;
    }
    public static class SimpleRequestJson{
        public String DeviceId;
    }

    public static class LocationJson{
        public String DeviceId;
        public double Latitude;
        public double Longitude;
        public String Provider;
        public String TrackingType;

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        public LocalDateTime LocationUpdatedTime;
    }
}
