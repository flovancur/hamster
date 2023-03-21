using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HSRM.CS.DistributedSystems.Hamster.Exceptions
{
    public class HamsterDatabaseCorruptedException : HamsterException
    {
        public HamsterDatabaseCorruptedException() : base("database is corrupted")
        {
        }
    }
}
