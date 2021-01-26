using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace CentralServer
{
    public class Settings
    {
        public const string DefaultDeviceId = "-1";
        public const string LatitudeMask = "{Lat}";
        public const string LongitudeMask = "{Long}";
        public static string WorkDirectory = Path.Combine(Directory.GetCurrentDirectory(), FileNames.WorkDirectoryFolderName);
        public static class FileNames
        {
            public const string ConfigFileName = "Config.json";
            public const string LocationsFileName = "Locations.json";
            public const string DevicesFileName = "Devices.json";
            public const string DevicesPingsFileName = "Pings.json";
            public const string DevicesTasksFileName = "Tasks.json";
            public const string TokensFileName = "Tokens.json";
            public const string WorkDirectoryFolderName = "ServerWorkDirectory";
        }
    }
}
