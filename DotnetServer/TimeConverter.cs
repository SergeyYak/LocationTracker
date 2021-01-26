using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CentralServer
{
    public static class TimeConverter
    {
        public static string ConvertTimeToUserFriendlyFormat(DateTime timeToConvert)
        {
            var now = DateTime.Now;
            var elapsedTime = now.Subtract(timeToConvert);

            var obj = new DateTimeObject();
            obj.TimeToConvert = elapsedTime;
            return obj.ConvertTime();
        }
    }
    internal class DateTimeObject
    {
        public TimeSpan TimeToConvert;
        private List<TimePair<double, string>> list = new List<TimePair<double, string>>();
        internal string ConvertTime()
        {
            GetDays();
            GetHours();
            GetMinutes();
            GetSeconds();

            var IntegerTime = list.Where(dict => dict.Mintime >= 1).ToList();
            TimePair<double, string> first = null;
            if (IntegerTime.Count > 0) first = IntegerTime[0];
            TimePair<double, string> second = null;
            if (IntegerTime.Count > 1) second = IntegerTime[1];

            string str = null;
            if (first != null) str = $"{first.Mintime} {first.Mindigit}";
            if (second != null) str += $" и {second.Mintime} {second.Mindigit}";

            if (str == null && list.Any())
            {
                var lastElem = list.Last();
                str = $"{lastElem.Mintime} {lastElem.Mindigit}";
            }
            return str;
        }

        internal void GetDays()
        {
            var pair = new TimePair<double, string>(TimeToConvert.Days, "дней");
            list.Add(pair);
        }
        internal void GetHours()
        {
            var pair = new TimePair<double, string>(TimeToConvert.Hours, "часов");
            list.Add(pair);
        }
        internal void GetMinutes()
        {
            var pair = new TimePair<double, string>(TimeToConvert.Minutes, "минут");
            list.Add(pair);
        }
        internal void GetSeconds()
        {
            var pair = new TimePair<double, string>(TimeToConvert.Seconds, "секунд");
            list.Add(pair);
        }
    };
    internal class TimePair<Double, String>
    {
        public double Mintime;
        public string Mindigit;

        public TimePair(double Mintime, string Mindigit)
        {
            this.Mintime = Mintime;
            this.Mindigit = Mindigit;
        }
    }
}
