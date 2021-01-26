using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CentralServer
{
    public class LocationJsonRoot
    {
        public List<DeviceLocation> DevicesLocations { get; set; } = new List<DeviceLocation>();
    }
    public class DeviceLocation
    {
        public string DeviceId { get; set; }
        public double Latitude { get; set; }
        public double Longitude { get; set; }
        public string Provider { get; set; }
        public string TrackingType { get; set; }
        public DateTime LocationUpdatedTime { get; set; }
        public string LocationUpdatedTimeSTR { get; set; }
        public string Link { get; set; }
    }
}
