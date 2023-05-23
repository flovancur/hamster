using CommandLine;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HamsterMqttClient
{
    [Verb("sleep", HelpText = "Lets the hamster sleep")]
    public class SleepVerb : VerbBase
    {
        protected override async Task<bool> Run(HamsterClient hamster)
        {
            // TODO
            return true;
        }
    }
}
