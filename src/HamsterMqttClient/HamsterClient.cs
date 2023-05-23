using MQTTnet;
using MQTTnet.Client;
using MQTTnet.Protocol;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Runtime.InteropServices;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;

namespace HamsterMqttClient
{
    public class HamsterClient
    {
        public Hamster Hamster { get; }

        public HamsterClient(Hamster hamster)
        {
            // TODO
            throw new NotImplementedException();
        }

        public async Task Connect(HamsterOptions options)
        {
            // TODO
            throw new NotImplementedException();
        }

        public Task Disconnect()
        {
            // TODO
            throw new NotImplementedException();
        }

        private static string GetCertsDirectory()
        {
            var dir = Path.GetFullPath(Environment.CurrentDirectory);
            while (!Directory.Exists(Path.Combine(dir!, "certs")))
            {
                dir = Path.GetDirectoryName(dir);
            }

            return Path.Combine(dir!, "certs");
        }
    }
}
