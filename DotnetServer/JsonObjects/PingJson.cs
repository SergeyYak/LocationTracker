using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CentralServer
{
    public class PingJsonRoot
    {
        public List<DevicePing> DevicesPings { get; set; } = new List<DevicePing>();
    }
    public class DevicePing
    {
        public string DeviceId { get; set; }
        public DateTime LastPingRequestTime { get; set; }
        public string LastPingRequestTimeSTR { get; set; }

    }
}
