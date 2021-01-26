using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.Extensions.Hosting.Internal;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace CentralServer.Pages
{
    public class LocationReceiverModel : PageModel
    {
        public async Task OnPostAsync()
        {
            var requestBody = Request.Body;
            using var reader = new StreamReader(requestBody);
            string requestBodyStr = await reader.ReadToEndAsync();
            await WriteToJson(requestBodyStr);
        }
        private static async Task WriteToJson(string requestBodyStr)
        {
            var workDirectory = Settings.WorkDirectory;
            var fileName = Settings.FileNames.LocationsFileName;
            var filePath = Path.Combine(workDirectory, fileName);

            DeviceLocation newDeviceLocation = JsonConvert.DeserializeObject<DeviceLocation>(requestBodyStr);

            string jsonFileStr = await FileManager.ReadFileAsync(filePath);

            LocationJsonRoot locationRootObj = new LocationJsonRoot();
            try
            {
                locationRootObj = JsonConvert.DeserializeObject<LocationJsonRoot>(jsonFileStr);
            }
            catch (ArgumentNullException) { }
            List<DeviceLocation> locationList = locationRootObj.DevicesLocations;

            int index = locationList.FindLastIndex(locationFromFile => locationFromFile.DeviceId == newDeviceLocation.DeviceId);
            if (index != -1) locationList[index] = newDeviceLocation;
            else locationList.Add(newDeviceLocation);
            string newFileStr = JsonConvert.SerializeObject(locationRootObj, Formatting.Indented);
            await FileManager.WriteFileAsync(filePath, newFileStr);
        }
    }
}
