using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HSRM.CS.DistributedSystems.Hamster
{
    public record HamsterState(
        int ID,
        short Cost,
        int Rounds,
        short TreatsLeft);
}
