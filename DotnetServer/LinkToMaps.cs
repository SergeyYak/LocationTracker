using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CentralServer
{
    public class LinkToMaps
    {
        private double Latitude;
        private double Longitude;
        private string LinkMask = Config.Instance.Link;
        public LinkToMaps(double latitude, double longitude)
        {
            Latitude = latitude;
            Longitude = longitude;
        }
        public string Link
        {
            get
            {
                return LinkMask.Replace(Settings.LatitudeMask, Latitude.ToString())
                .Replace(Settings.LongitudeMask, Longitude.ToString());
            }
        }
    }
}
