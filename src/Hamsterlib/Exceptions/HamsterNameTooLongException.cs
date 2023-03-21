using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HSRM.CS.DistributedSystems.Hamster.Exceptions
{
    public class HamsterNameTooLongException : HamsterException
    {
        public HamsterNameTooLongException() : base("the specified name is too long")
        {
        }
    }
}
