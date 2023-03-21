using HSRM.CS.DistributedSystems.Hamster.Exceptions;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Xml.Serialization;

namespace HSRM.CS.DistributedSystems.Hamster
{
    /// <summary>
    /// HamsterDataStores manages @see DataStoreEntry objects. On creation entiers
    /// will be read from hamsterdatastore.xml.Modifikations made to the dataStore
    /// result in an update of hamsterdatastore.xml
    /// </summary>
    public class HamsterDataStore
    {
        /// <summary>
        /// single DataStore instance
        /// </summary>
        private static readonly HamsterDataStore _instance = new HamsterDataStore();

        /// <summary>
        /// Singleton pattern to create a single DataStore instance
        /// </summary>
        public static HamsterDataStore Instance => _instance;

        // make default constructor private
        private HamsterDataStore()
        {
            ReadFromXml();
        }

        private readonly List<DataStoreEntry> _entries = new List<DataStoreEntry>();

        public IEnumerable<DataStoreEntry> Entries => _entries.Select(e => e.Copy());

        /// <summary>
        /// Creates a hash code of both parameters
        /// </summary>
        /// <param name="ownerName">Name of the owner</param>
        /// <param name="hamsterName">Name of the hamster</param>
        /// <returns>hash code generated from both parameters</returns>
        private int CreateHash(string ownerName, string hamsterName)
        {
            var combinedString = ownerName.PadRight(HamsterManagement.MaxNameLength, '\0') + hamsterName.PadRight(HamsterManagement.MaxNameLength, '\0');
            uint hash = 5381;
            unchecked 
            {
                for (int i = 0; i < combinedString.Length; i++)
                {
                    hash = 33 * hash + combinedString[i];
                }
            }
            return (int)(hash >> 1);
        }

        /// <summary>
        /// Write a new entry into the DataStore
        /// </summary>
        /// <param name="ownerName">name of the owner</param>
        /// <param name="hamsterName">name of the hamster</param>
        /// <param name="treats">avaliable treats</param>
        /// <returns>id of the created entry</returns>
        /// <exception cref="HamsterExistsException">Thrown if a hamster with this name already exists</exception>
        public int AddEntry(string ownerName, string hamsterName, short treats)
        {
            var id = CreateHash(ownerName, hamsterName);

            if (EntryExists(id))
            {
                throw new HamsterExistsException();
            }

            _entries.Add(new DataStoreEntry(id, ownerName, hamsterName, treats));

            WriteToXml();

            return id;
        }

        /// <summary>
        /// Searches for the given id and returns true ifthe entry exists
        /// </summary>
        /// <param name="id">the id to search for</param>
        /// <returns>true if an entry with the given id exists, else false</returns>
        public bool EntryExists(int id)
        {
            return _entries.Any(e => e.ID == id);
        }

        /// <summary>
        /// Searches for the given id and returns the DataStoreEntry object or null
        /// </summary>
        /// <param name="id">the id to search for</param>
        /// <returns>DataStoreEntry</returns>
        /// <exception cref="HamsterNotFoundException">Thrown if the hamster was not found</exception>
        public DataStoreEntry GetEntryById(int id)
        {
            var entry = _entries.FirstOrDefault(e => e.ID == id);
            if (entry == null)
            {
                throw new HamsterNotFoundException();
            }
            else
            {
                return entry.Copy();
            }
        }

        /// <summary>
        /// Removes the entry with the given id
        /// </summary>
        /// <param name="id">id to remove</param>
        public void RemoveEntryById(int id)
        {
            // a foreach loop does not work in .NET because the list will check that it is not modified while enumerated
            for (int i = _entries.Count - 1; i >= 0; i--)
            {
                if (_entries[i].ID == id)
                {
                    _entries.RemoveAt(i);
                    WriteToXml();
                    return;
                }
            }
            throw new HamsterNotFoundException();
        }

        public void Clear()
        {
            _entries.Clear();
            WriteToXml();
        }

        public void UpdateEntry(DataStoreEntry entry)
        {
            var myEntry = _entries.FirstOrDefault(e => e.ID == entry.ID);
            if (myEntry == null)
            {
                throw new HamsterNotFoundException();
            }

            myEntry.Price = entry.Price;
            myEntry.Treats = entry.Treats;
            WriteToXml();
        }

        private void ReadFromXml()
        {
            try
            {
                using (var stream = File.OpenRead("hamsterdatastore.xml"))
                {
                    var serializer = new XmlSerializer(typeof(Root));
                    var deserialized = serializer.Deserialize(stream) as Root;
                    if (deserialized != null)
                    {
                        _entries.AddRange(deserialized);
                    }
                }
            }
            catch (FileNotFoundException)
            {
                // start with no hamsters, if file not found
            }
            catch (Exception ex)
            {
                throw new HamsterStorageException(ex);
            }
        }

        private void WriteToXml()
        {
            try
            {
                using (var stream = File.Create("hamsterdatastore.xml"))
                {
                    var serializer = new XmlSerializer(typeof(Root));
                    var root = new Root();
                    root.AddRange(_entries);
                    serializer.Serialize(stream, root);
                }
            }
            catch (Exception ex)
            {
                throw new HamsterStorageException(ex);
            }
        }

        [XmlRoot(ElementName = nameof(HamsterDataStore))]
        public class Root : List<DataStoreEntry> { }
    }
}
