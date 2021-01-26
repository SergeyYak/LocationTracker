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
    public class TaskSenderModel : PageModel
    {

        public async Task<ContentResult> OnPostAsync()
        {
            var requestBody = Request.Body;
            using var reader = new StreamReader(requestBody);
            string requestBodyStr = await reader.ReadToEndAsync();
            var result = await GetCurrentDeviceTask(requestBodyStr);
            return Content(result);
        }

        private async Task<string> GetCurrentDeviceTask(string requestBodyStr)
        {
            string workDirectory = Settings.WorkDirectory;
            string fileName = Settings.FileNames.DevicesTasksFileName;
            string filePath = Path.Combine(workDirectory, fileName);

            string jsonFileStr = await FileManager.ReadFileAsync(filePath);
            DeviceTask newDeviceTask = JsonConvert.DeserializeObject<DeviceTask>(requestBodyStr);

            await WriteLastRequestTimeAsync(newDeviceTask.DeviceId);

            TaskJsonRoot taskRootObj = new TaskJsonRoot();
            try
            {
                taskRootObj = JsonConvert.DeserializeObject<TaskJsonRoot>(jsonFileStr);
            }
            catch (ArgumentNullException) { }
            List<DeviceTask> devicesTasks = taskRootObj.DevicesTasks;

            DeviceTask task = devicesTasks
                .Where(deviceTaskFromFile => deviceTaskFromFile.DeviceId == newDeviceTask.DeviceId)
                .SingleOrDefault();

            if (task != null && !task.TaskSended)
            {
                task.TaskSended = true;
                string newTaskFileStr = JsonConvert.SerializeObject(taskRootObj, Formatting.Indented);
                await FileManager.WriteFileAsync(filePath, newTaskFileStr);

                string taskStrForDevice = JsonConvert.SerializeObject(task);
                return taskStrForDevice;
            }
            else return null;
        }

        private async Task WriteLastRequestTimeAsync(string currentDeviceId)
        {
            var workDirectory = Settings.WorkDirectory;
            var fileName = Settings.FileNames.DevicesPingsFileName;
            var filePath = Path.Combine(workDirectory, fileName);

            string jsonFileStr = await FileManager.ReadFileAsync(filePath);

            PingJsonRoot pingRootObj = new PingJsonRoot();
            try
            {
                pingRootObj = JsonConvert.DeserializeObject<PingJsonRoot>(jsonFileStr);
            }
            catch (ArgumentNullException) { }

            List<DevicePing> devicesPings = pingRootObj.DevicesPings;
            DevicePing newDevicePing = new DevicePing();
            newDevicePing.DeviceId = currentDeviceId;
            newDevicePing.LastPingRequestTime = DateTime.Now;

            int index = devicesPings.FindLastIndex(devicePingFromFile => devicePingFromFile.DeviceId == currentDeviceId);
            if (index != -1) devicesPings[index] = newDevicePing;
            else devicesPings.Add(newDevicePing);

            string newFileStr = JsonConvert.SerializeObject(pingRootObj, Formatting.Indented);
            await FileManager.WriteFileAsync(filePath, newFileStr);
        }
    }
}