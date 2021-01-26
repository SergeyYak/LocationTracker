using Microsoft.AspNetCore.Http;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Threading.Tasks;

namespace CentralServer
{
    public static class Auth
    {
        public static string AuthPagePath = "/Auth";
        private static string AuthCookieName = "AuthToken";

        private static string workDirectory = Settings.WorkDirectory;
        private static string fileName = Settings.FileNames.TokensFileName;
        private static string filePath = Path.Combine(workDirectory, fileName);
        public static async Task<bool> CheckIfAuthorized(HttpRequest request, HttpResponse response)
        {
            bool authCookieExists = request.Cookies.ContainsKey(AuthCookieName);
            bool tokenExists = await CheckIfTokenExists(request.Cookies[AuthCookieName]);
            if (Config.Instance.AskPasswordEveryTime) CancelAuth(response);
            return (authCookieExists && tokenExists);
        }

        public static async Task<bool> AuthorizeAsync(string login, string password, HttpRequest request, HttpResponse response)
        {
            if (login == Config.Instance.Login && password == Config.Instance.Password)
            {
                Cookie cookie = new Cookie();
                cookie.Name = AuthCookieName;
                cookie.Value = await CreateToken();

                CookieOptions cookieOptions = new CookieOptions();
                cookieOptions.Path = "/";
                cookieOptions.Expires = DateTime.Now.AddDays(Config.Instance.RememberAuthDays);
                cookieOptions.HttpOnly = true;
                cookieOptions.Secure = true;
                if (request.Cookies.ContainsKey(cookie.Name))
                {
                    CancelAuth(response);
                }

                response.Cookies.Append(cookie.Name, cookie.Value, cookieOptions);

                return true;
            }
            else return false;
        }

        public static void CancelAuth(HttpResponse response)
        {
            response.Cookies.Delete(AuthCookieName);
        }

        private static async Task<string> CreateToken()
        {
            string jsonFileStr = await FileManager.ReadFileAsync(filePath);

            TokenJsonRoot tokenRootObj = new TokenJsonRoot();
            try
            {
                tokenRootObj = JsonConvert.DeserializeObject<TokenJsonRoot>(jsonFileStr);
            }
            catch (ArgumentNullException)
            { }

            List<TokenObj> tokens = tokenRootObj.tokens;
            TokenObj tokenObj = new TokenObj();
            tokenObj.AuthToken = GenerateToken();
            tokens.Add(tokenObj);

            string newFileStr = JsonConvert.SerializeObject(tokenRootObj, Formatting.Indented);
            await FileManager.WriteFileAsync(filePath, newFileStr);
            return tokenObj.AuthToken;

            string GenerateToken()
            {
                byte[] time = BitConverter.GetBytes(DateTime.UtcNow.ToBinary());
                byte[] key = Guid.NewGuid().ToByteArray();
                string token = Convert.ToBase64String(time.Concat(key).ToArray());
                return token;
            }
        }
        private static async Task<bool> CheckIfTokenExists(string cookieToken)
        {
            string jsonFileStr = await FileManager.ReadFileAsync(filePath);

            TokenJsonRoot tokenRootObj = new TokenJsonRoot();
            try
            {
                tokenRootObj = JsonConvert.DeserializeObject<TokenJsonRoot>(jsonFileStr);
            }
            catch (ArgumentNullException)
            { }

            List<TokenObj> tokens = tokenRootObj.tokens;
            int index = tokens.FindLastIndex(tokenFromFile => tokenFromFile.AuthToken == cookieToken);
            if (index == -1) return false;
            else return true;
        }
    }
}