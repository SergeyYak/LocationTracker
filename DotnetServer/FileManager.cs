using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;

namespace CentralServer
{
    public static class FileManager
    {
        public static async Task WriteFileAsync(string path, string content)
        {
            CheckDirectory(path);
            await File.WriteAllTextAsync(path, content);
        }
        public static async Task<string> ReadFileAsync(string path)
        {
            CheckDirectory(path);
            var file = new FileInfo(path);
            if (!file.Exists)
            {
                return null;
            }
            string readedFile = await File.ReadAllTextAsync(path);
            return readedFile;
        }
        private static void CheckDirectory(string path)
        {
            var directory = Path.GetDirectoryName(path);
            if (!Directory.Exists(directory))
            {
                Directory.CreateDirectory(directory);
            }
        }
    }
}