using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Newtonsoft.Json;

namespace CentralServer.Pages
{
    public class AuthModel : PageModel
    {
        public bool ClientAuthorized = false;
        public bool credentialsCorrect = true;

        public async Task OnGetAsync()
        {
            ClientAuthorized = await Auth.CheckIfAuthorized(Request, Response);
        }
        public async Task<IActionResult> OnPostAsync(string redirectToBack, string login, string password)
        {
            ClientAuthorized =  await Auth.AuthorizeAsync(login, password, Request, Response);
            credentialsCorrect = ClientAuthorized;
            if (ClientAuthorized) return RedirectToPage(redirectToBack);
            else return null;
        }
    }
}