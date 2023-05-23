using CommandLine;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HamsterMqttClient
{
    [Verb("move", HelpText = "Lets the hamster move")]
    public class MoveVerb : VerbBase
    {
        [Value(0, Required = false, Default = "A")]
        public string Location { get; set; } = "A";

        protected override async Task<bool> Run(HamsterClient hamster)
        {
            // TODO
            return true;
        }
    }
}
