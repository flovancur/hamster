using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HSRM.CS.DistributedSystems.Hamster.Exceptions
{
    public class HamsterNotFoundException : HamsterException
    {
        public HamsterNotFoundException() : base("A hamster or hamster owner could not be found.")
        {
        }
    }
}
