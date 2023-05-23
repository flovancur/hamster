using MQTTnet.Client;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HamsterMqttClient
{
    public abstract class VerbBase
    {
        public async Task<bool> TryRun(HamsterClient client)
        {
            try
            {
                return await Run(client);
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine(ex.Message);
                return true;
            }
        }

        protected abstract Task<bool> Run(HamsterClient hamster);
    }
}
