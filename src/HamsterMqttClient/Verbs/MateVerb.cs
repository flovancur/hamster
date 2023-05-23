using CommandLine;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HamsterMqttClient
{
    [Verb("mate", HelpText = "Lets the hamster mate")]
    public class MateVerb : VerbBase
    {
        protected override async Task<bool> Run(HamsterClient hamster)
        {
            // TODO
            return true;

        }
    }
}
