using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Newtonsoft.Json;

namespace CentralServer.Pages
{
    public class MainModel : PageModel
    {
        public bool ClientAuthorized = false;
        public List<Device> Devices { get; set; } = new List<Device>();
        public List<DeviceLocation> DevicesLocations { get; set; } = new List<DeviceLocation>();
        public List<DeviceTask> DevicesTasks { get; set; } = new List<DeviceTask>();
        public List<DevicePing> DevicesPings { get; set; } = new List<DevicePing>();

        public async Task<IActionResult> OnGetAsync()
        {
            if (await Auth.CheckIfAuthorized(Request, Response))
            {
                ClientAuthorized = true;
                await ShowContent();
                return null;
            }
            else return RedirectToPage(Auth.AuthPagePath, new { redirectToBack = Request.Path });
        }

        public IActionResult OnPost(string exitBtn)
        {
            if (exitBtn != null && Boolean.Parse(exitBtn) == true)
            {
                Auth.CancelAuth(Response);
                return RedirectToPage(Auth.AuthPagePath, new { redirectToBack = Request.Path });
            }
            else return null;
        }
        public async Task ShowContent()
        {
            var workDirectory = Settings.WorkDirectory;

            var locationFileName = Settings.FileNames.LocationsFileName;
            var pathToLocationFile = Path.Combine(workDirectory, locationFileName);

            var DevicesFileName = Settings.FileNames.DevicesFileName;
            var pathToDevicesFileName = Path.Combine(workDirectory, DevicesFileName);

            var DevicesTasksFileName = Settings.FileNames.DevicesTasksFileName;
            var pathToTasksFile = Path.Combine(workDirectory, DevicesTasksFileName);

            var DevicesPingsFileName = Settings.FileNames.DevicesPingsFileName;
            var pathToPingsFileName = Path.Combine(workDirectory, DevicesPingsFileName);

            string locationJsonStr = await FileManager.ReadFileAsync(pathToLocationFile);
            string devicesJsonStr = await FileManager.ReadFileAsync(pathToDevicesFileName);
            string tasksJsonStr = await FileManager.ReadFileAsync(pathToTasksFile);
            string pingsJsonStr = await FileManager.ReadFileAsync(pathToPingsFileName);

            LocationJsonRoot locationJsonObj = new LocationJsonRoot();
            DeviceJsonRoot devicesJsonObj = new DeviceJsonRoot();
            TaskJsonRoot taskJsonObj = new TaskJsonRoot();
            PingJsonRoot pingJsonObj = new PingJsonRoot();

            try
            {
                locationJsonObj = JsonConvert.DeserializeObject<LocationJsonRoot>(locationJsonStr);
            }
            catch (ArgumentNullException) { }

            try
            {
                devicesJsonObj = JsonConvert.DeserializeObject<DeviceJsonRoot>(devicesJsonStr);
            }
            catch (ArgumentNullException) { }

            try
            {
                taskJsonObj = JsonConvert.DeserializeObject<TaskJsonRoot>(tasksJsonStr);
            }
            catch (ArgumentNullException) { }

            try
            {
                pingJsonObj = JsonConvert.DeserializeObject<PingJsonRoot>(pingsJsonStr);
            }
            catch (ArgumentNullException) { }

            locationJsonObj.DevicesLocations
                .ForEach(location =>
                {
                    location.LocationUpdatedTimeSTR = TimeConverter.
                        ConvertTimeToUserFriendlyFormat(location.LocationUpdatedTime);
                    location.Link = new LinkToMaps(location.Latitude, location.Longitude).Link;
                });

            pingJsonObj.DevicesPings
                .ForEach(devicePing =>
                {
                    devicePing.LastPingRequestTimeSTR = TimeConverter
                    .ConvertTimeToUserFriendlyFormat(devicePing.LastPingRequestTime);
                });

            Devices.AddRange(devicesJsonObj.Devices);
            DevicesLocations.AddRange(locationJsonObj.DevicesLocations);
            DevicesTasks.AddRange(taskJsonObj.DevicesTasks);
            DevicesPings.AddRange(pingJsonObj.DevicesPings);
        }
    }
}