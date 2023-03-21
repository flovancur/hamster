using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Serialization;

namespace HSRM.CS.DistributedSystems.Hamster
{
    public class DataStoreEntry
    {
        public DataStoreEntry() { }

        public DataStoreEntry(int id, string ownerName, string hamsterName, short treats)
        {
            ID = id;
            OwnerName = ownerName;
            HamsterName = hamsterName;
            Treats = treats;
            AdmissionTime = DateTime.Now;
        }

        [XmlElement("id")]
        public int ID { get; set; }

        [XmlElement("admissionTime")]
        public DateTime AdmissionTime { get; set; }

        [XmlElement("ownerName")]
        public string OwnerName { get; set; }

        [XmlElement("hamsterName")]
        public string HamsterName { get; set; }

        [XmlElement("price")]
        public short Price { get; set; } = HamsterManagement.BasePrice;

        [XmlElement("treats")]
        public short Treats { get; set; } = 0;

        public DataStoreEntry Copy() => new DataStoreEntry
        {
            ID = ID,
            AdmissionTime = AdmissionTime,
            OwnerName = OwnerName,
            HamsterName = HamsterName,
            Price = Price,
            Treats = Treats,
        };
    }
}
