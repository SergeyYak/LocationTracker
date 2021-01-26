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
    public class TaskReceiverModel : PageModel
    {
        private string DeviceId;
        private uint TaskCode;
        private double TimeInMs;
        private uint Distance;
        public async Task OnPostAsync(string deviceId, string buttonValue, string hours, string minutes, string seconds, string distance)
        {
            uint hh = uint.Parse(hours);
            uint mm = uint.Parse(minutes);
            uint ss = uint.Parse(seconds);
            double milliseconds = Math.Round(
                TimeSpan.FromHours(hh).TotalMilliseconds
                + TimeSpan.FromMinutes(mm).TotalMilliseconds
                + TimeSpan.FromSeconds(ss).TotalMilliseconds
                );

            DeviceId = deviceId;
            TaskCode = uint.Parse(buttonValue);
            TimeInMs = milliseconds;
            Distance = uint.Parse(distance);
            await WriteTask();
        }

        private async Task WriteTask()
        {
            var workDirectory = Settings.WorkDirectory;
            var fileName = Settings.FileNames.DevicesTasksFileName;
            var filePath = Path.Combine(workDirectory, fileName);

            string jsonFileStr = await FileManager.ReadFileAsync(filePath);

            DeviceTask task = new DeviceTask();
            task.DeviceId = DeviceId;
            task.TaskCode = TaskCode;
            task.Time = TimeInMs;
            task.Distance = Distance;
            TaskJsonRoot taskRootObj = new TaskJsonRoot();
            try
            {
                taskRootObj = JsonConvert.DeserializeObject<TaskJsonRoot>(jsonFileStr);
            }
            catch (ArgumentNullException)
            { }

            List<DeviceTask> deviceTaskList = taskRootObj.DevicesTasks;

            int index = deviceTaskList.FindLastIndex(deviceTaskFromFile => deviceTaskFromFile.DeviceId == DeviceId);
            if (index != -1) deviceTaskList[index] = task;
            else deviceTaskList.Add(task);
            string newFileStr = JsonConvert.SerializeObject(taskRootObj, Formatting.Indented);
            await FileManager.WriteFileAsync(filePath, newFileStr);
        }
    }
}