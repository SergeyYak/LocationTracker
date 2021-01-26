using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CentralServer
{
    public class TokenJsonRoot
    {
        public List<TokenObj> tokens { get; set; } = new List<TokenObj>();
    }

    public class TokenObj
    {
        public string AuthToken { get; set; }
    }
}
