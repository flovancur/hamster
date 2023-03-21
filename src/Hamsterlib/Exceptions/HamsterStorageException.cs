using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HSRM.CS.DistributedSystems.Hamster.Exceptions
{
    public class HamsterStorageException : HamsterException
    {
        public HamsterStorageException() : this(null)
        {
        }

        public HamsterStorageException(Exception? innerException) : base("storage error", innerException)
        {
        }
    }
}
