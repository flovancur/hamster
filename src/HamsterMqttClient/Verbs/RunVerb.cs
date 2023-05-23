using CommandLine;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HamsterMqttClient
{
    [Verb("run", HelpText = "Lets the hamster run")]
    public class RunVerb : VerbBase
    {
        protected override async Task<bool> Run(HamsterClient hamster)
        {
            // TODO
            return true;
        }
    }
}
