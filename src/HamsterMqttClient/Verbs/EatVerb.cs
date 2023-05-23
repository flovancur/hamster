using CommandLine;
using MQTTnet.Client;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HamsterMqttClient
{
    [Verb("eat", HelpText = "Lets the hamster eat")]
    public class EatVerb : VerbBase
    {
        protected override async Task<bool> Run(HamsterClient hamster)
        {
            // TODO
            return true;
        }
    }
}
