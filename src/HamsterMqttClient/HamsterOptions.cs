using CommandLine;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HamsterMqttClient
{
    public class HamsterOptions
    {
        [Value(0, Required = true, HelpText = "The id for the hamster that the client is attached to")]
        public int HamsterId { get; set; }

        [Option('t', "target", Required = false, HelpText = "The address of the MQTT broker server")]
        public string Target { get; set; } = "127.0.0.1";

        [Option('s', "silent", Required = false, HelpText = "Omits the explanation of commands")]
        public bool Silent { get; set; }

        [Option('e', "encrypted", HelpText = "If set, the connection uses a TLS encrypted channel", Required = false)]
        public bool Encrypt { get; set; }

        [Option('c', "client-certificate", HelpText = "The path to the client certificate", Required = false)]
        public string ClientCertificate { get; set; }
    }
}
