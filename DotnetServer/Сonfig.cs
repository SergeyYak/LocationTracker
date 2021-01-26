using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace CentralServer
{
    public class Config
    {
        private readonly static string configFilePath = Path.Combine(Settings.WorkDirectory, Settings.FileNames.ConfigFileName);

        private static Config instance;
        public static Config Instance
        {
            get { return instance ?? ReadConfig(); }
            private set { instance = value; }
        }

        private static Config ReadConfig()
        {
            string context = File.ReadAllText(configFilePath);
            return JsonConvert.DeserializeObject<Config>(context);
        }

        public string Login { get; set; }
        public string Password { get; set; }
        public bool AskPasswordEveryTime { get; set; }
        public uint RememberAuthDays { get; set; }
        public int Port { get; set; }
        public string HttpsCertificateName { get; set; }
        public string HttpsCertificatePassword { get; set; }
        public string Link { get; set; }
    }
}
