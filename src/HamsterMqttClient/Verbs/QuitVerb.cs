using CommandLine;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HamsterMqttClient
{
    [Verb("quit", HelpText = "quits the client")]
    public class QuitVerb : VerbBase
    {
        protected override Task<bool> Run(HamsterClient hamster)
        {
            Console.WriteLine("Goodbye");
            return Task.FromResult(false);
        }
    }
}
