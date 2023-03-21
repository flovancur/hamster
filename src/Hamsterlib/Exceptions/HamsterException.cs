using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HSRM.CS.DistributedSystems.Hamster.Exceptions
{
    public class HamsterException : Exception
    {
        public HamsterException(string? message) : base(message)
        {
        }

        public HamsterException(string? message, Exception? innerException) : base(message, innerException)
        {
        }
    }
}
