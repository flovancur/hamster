using HSRM.CS.DistributedSystems.Hamster.Exceptions;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection.PortableExecutable;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;
using System.Xml;
using System.Xml.Linq;
using static System.Runtime.InteropServices.JavaScript.JSType;
using System.Diagnostics;
using System.Security.Cryptography;

namespace HSRM.CS.DistributedSystems.Hamster
{
    public class HamsterManagement
    {
        internal const int MaxNameLength = 31;
        private const int Rpm = 25;
        internal const short BasePrice = 17;
        private const short StayPrice = 5;
        private const short FeedPrice = 2;
        private const short CarePrice = 1;

        private readonly HamsterDataStore _dataStore = HamsterDataStore.Instance;

        /// <summary>
        /// Put a new hamster into the hamstercare institute
        /// 
        /// This function makes a new entry into the hamster database.It returns
        /// a unique ID by which the hamster can later be referenced. A Hamster
        /// may be given a number of treats to feed.
        /// </summary>
        /// <param name="ownerName">name of hamster's owner</param>
        /// <param name="hamsterName">name of hamster</param>
        /// <param name="treats">initial provision of treats</param>
        /// <returns>Unique ID (always >= 0) of the new entry</returns>
        /// <exception cref="HamsterNameTooLongException"></exception>
        public int NewHamster(string ownerName, string hamsterName, short treats)
        {
            if (ownerName.Length > MaxNameLength || hamsterName.Length > MaxNameLength)
            {
                throw new HamsterNameTooLongException();
            }

            if (treats < 0)
            {
                treats = 0;
            }

            if (_dataStore.Entries.Any(e => e.OwnerName == ownerName && e.HamsterName == hamsterName))
            {
                throw new HamsterExistsException();
            }

            return _dataStore.AddEntry(ownerName, hamsterName, treats);
        }

        /// <summary>
        /// Find hamster in the hamstercare institute
        /// 
        /// This function locates an entry in the hamster database.It returns a
        /// unique ID by which the hamster can be referenced. A Hamster is
        /// uniquely identified by the combination of the owner's name and the
        /// hamster's name.
        /// </summary>
        /// <param name="ownerName">name of hamster's owner</param>
        /// <param name="hamsterName">name of hamster</param>
        /// <returns>If successful: Unique ID (always >= 0) of the entry</returns>
        /// <exception cref="HamsterNameTooLongException"></exception>
        public int Lookup(string ownerName, string hamsterName)
        {
            if (ownerName.Length > MaxNameLength || hamsterName.Length > MaxNameLength)
            {
                throw new HamsterNameTooLongException();
            }

            var entry = _dataStore.Entries.FirstOrDefault(e => e.OwnerName == ownerName && e.HamsterName == hamsterName);
            if (entry == null)
            {
                throw new HamsterNotFoundException();
            }
            else
            {
                return entry.ID;
            }
        }

        /// <summary>
        /// Get a directory of entries in the database
        /// 
        /// This function enables a "wildcard search" of the database.It delivers
        /// UIDs of matching entries.The caller may specify an owner name or a
        /// hamster name, thus specifying a particular entry (in this case the
        /// function is similar to hmstr_lookup()). However, it is also possible
        /// to specify __only__ an owner name or __only__ a hamster name by
        /// passing a NULL value for the name that should not be specified.In
        /// this case, the function delivers UIDs of __all__ entries matching the
        /// specified name.If both names are passed as NULL, the function
        /// delivers the UIDs of __all__ entries in the database.
        /// 
        /// The function delivers an enumeration of all hamsters that match
        /// the given criteria
        /// </summary>
        /// <param name="ownerName">name of hamster's owner or null if not specified</param>
        /// <param name="hamsterName">name of hamster or null if not specified</param>
        /// <returns>A collection of IDs for hamsters</returns>
        /// <exception cref="HamsterNameTooLongException"></exception>
        public IEnumerable<int> Search(string? ownerName = null, string? hamsterName = null)
        {
            if ((ownerName != null && ownerName.Length > MaxNameLength)
                || (hamsterName != null && hamsterName.Length > MaxNameLength))
            {
                throw new HamsterNameTooLongException();
            }

            IEnumerable<DataStoreEntry> entries = _dataStore.Entries;
            if (ownerName != null)
            {
                entries = entries.Where(e => e.OwnerName == ownerName);
            }
            if (hamsterName != null)
            {
                entries = entries.Where(e => e.HamsterName == hamsterName);
            }
            return entries.Select(e => e.ID);
        }

        /// <summary>
        /// How is my hamster doing?
        /// 
        /// This function checks upon hamster (at a cost!) identified by ID. It
        /// returns the hamster's state in the given data structure.
        /// </summary>
        /// <param name="id">Hamster's unique ID</param>
        /// <returns>data structure where to store information</returns>
        public HamsterState Howsdoing(int id)
        {
            var entry = _dataStore.GetEntryById(id);
            int rounds = GetRounds(entry);

            entry.Price += CarePrice;
            _dataStore.UpdateEntry(entry);

            return new HamsterState(entry.ID, entry.Price, rounds, entry.Treats);
        }

        private int GetRounds(DataStoreEntry entry)
        {
            var duration = (int)((DateTime.Now - entry.AdmissionTime).TotalSeconds);
            var rounds = (duration * Rpm) / 60;
            return rounds;
        }

        /// <summary>
        /// Get contents of an entry in the database
        /// 
        /// This function delivers details of a hamster identified by UID.The
        /// price is __not__ changed by a call to this function.
        /// </summary>
        /// <param name="id">Hamster's unique ID</param>
        /// <param name="ownerName">where to store the owner's name</param>
        /// <param name="hamsterName">where to store the hamster's name</param>
        /// <param name="price">where to store the price</param>
        /// <returns>number of treats left in hamster' store</returns>
        public short ReadEntry(int id, out string ownerName, out string hamsterName, out short price)
        {
            var entry = _dataStore.GetEntryById(id);

            ownerName = entry.OwnerName;
            hamsterName = entry.HamsterName;
            price = entry.Price;

            return entry.Treats;
        }

        /// <summary>
        /// Give treats to my hamster
        /// 
        /// This function gives treats to the hamster identified by ID. The
        /// Hamster's stock of treats will be used up first. If stock is
        /// insufficient, more treats will be dispensed (at a cost!) and the
        /// function returns a benign error.
        /// </summary>
        /// <param name="id">Hamster's unique ID</param>
        /// <param name="treats">How many treats to feed</param>
        /// <returns>number of treats left in stock (always >=0)</returns>
        public short GiveTreats(int id, short treats)
        {
            if (treats < 0)
            {
                treats = 0;
            }

            var entry = _dataStore.GetEntryById(id);
            entry.Treats -= treats;

            if (entry.Treats < 0)
            {
                entry.Price -= (short)(entry.Treats * FeedPrice);
                entry.Treats = 0;
            }
            _dataStore.UpdateEntry(entry);
            return entry.Treats;
        }

        /// <summary>
        /// Collect all my hamsters and pay the bill
        /// 
        /// This function collects(i.e.deletes from the database) all hamsters
        /// owned by the specified owner and sums up all their expenses to produce
        /// a final bill.
        /// </summary>
        /// <param name="ownerName">name of hamster owner</param>
        /// <returns>number of euros to pay</returns>
        /// <exception cref="HamsterNameTooLongException"></exception>
        public short Collect(string ownerName)
        {
            if (ownerName.Length > MaxNameLength)
            {
                throw new HamsterNameTooLongException();
            }

            var hamsters = Search(ownerName: ownerName).ToList();

            if (!hamsters.Any())
            {
                throw new HamsterNotFoundException();
            }

            var price = hamsters.Sum(i =>
            {
                var entry = _dataStore.GetEntryById(i);

                return entry.Price + (GetRounds(entry) * StayPrice) / 1000;
            });

            foreach (var hamster in hamsters)
            {
                _dataStore.RemoveEntryById(hamster);
            }
            return (short)price;
        }
    }
}
