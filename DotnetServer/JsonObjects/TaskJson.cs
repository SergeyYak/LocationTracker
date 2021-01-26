using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CentralServer
{
    public class TaskJsonRoot
    {
        public List<DeviceTask> DevicesTasks { get; set; } = new List<DeviceTask>();
    }
    public class DeviceTask
    {
        public string DeviceId { get; set; }
        public uint TaskCode { get; set; }
        public double Time { get; set; }
        public uint Distance { get; set; }
        public bool TaskSended { get; set; } = false;
    }

    public static class TaskCodes
    {
        public static uint GPS_Once_Code = 1;
        public static uint GPS_Track_Code = 2;
        public static uint Mobile_Operator_Once_Code = 3;
        public static uint Mobile_Operator_Track_Code = 4;
        public static uint Passive_Track_Code = 5;
        public static uint GetLastKnownLocationCode = 6;
        public static uint DisableAllCode = 7;
    }
}
