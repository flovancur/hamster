using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HSRM.CS.DistributedSystems.Hamster.Exceptions
{
    public class HamsterExistsException : HamsterException
    {
        public HamsterExistsException() : base("a hamster by that owner/name already exists")
        {
        }
    }
}
