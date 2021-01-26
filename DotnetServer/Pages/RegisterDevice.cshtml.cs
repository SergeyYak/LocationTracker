using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Newtonsoft.Json;

namespace CentralServer.Pages
{
    public class RegisterDeviceModel : PageModel
    {
        private static readonly int tickCount = Environment.TickCount;
        private static readonly Random randomObj = new Random(tickCount);

        private static readonly int minValue = 100000000;
        private static readonly int maxValue = 999999999;
        public async Task<ContentResult> OnPostAsync()
        {
            var requestBody = Request.Body;
            string deviceId = null;
            using (var reader = new StreamReader(requestBody))
            {
                string requestBodyStr = await reader.ReadToEndAsync();
                deviceId = await RegisterDeviceAsync(requestBodyStr);
            }
            return Content(deviceId);
        }
        private static async Task<string> RegisterDeviceAsync(string requestBodyStr)
        {
            var workDirectory = Settings.WorkDirectory;
            var fileName = Settings.FileNames.DevicesFileName;
            var filePath = Path.Combine(workDirectory, fileName);

            Device newDevice = JsonConvert.DeserializeObject<Device>(requestBodyStr);

            string jsonFileStr = await FileManager.ReadFileAsync(filePath);

            DeviceJsonRoot devicesRootObj = new DeviceJsonRoot();
            try
            {
                devicesRootObj = JsonConvert.DeserializeObject<DeviceJsonRoot>(jsonFileStr);
            }
            catch(ArgumentNullException) { }
            bool isValueAlreadyExists = true;

            string randomValue = Settings.DefaultDeviceId;
            while (isValueAlreadyExists)
            {
                randomValue = randomObj.Next(minValue, maxValue).ToString();
                isValueAlreadyExists = devicesRootObj.Devices.Select(DeviceFromFile => DeviceFromFile.DeviceId.Contains(randomValue)).FirstOrDefault();
            }
            Device deviceObj = new Device();
            deviceObj.DeviceId = randomValue;
            deviceObj.DeviceName = newDevice.DeviceName;
            devicesRootObj.Devices.Add(deviceObj);
            
            string newFileStr = JsonConvert.SerializeObject(devicesRootObj, Formatting.Indented);
            await FileManager.WriteFileAsync(filePath, newFileStr);
            return randomValue;
        }
    }
}