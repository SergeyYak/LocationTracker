using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CentralServer
{
    public class DeviceJsonRoot
    {
        public List<Device> Devices { get; set; } = new List<Device>();
    }

    public class Device
    {
        public string DeviceName { get; set; }
        public string DeviceId { get; set; }
    }
}
