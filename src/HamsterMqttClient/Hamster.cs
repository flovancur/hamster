using MQTTnet.Protocol;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HamsterMqttClient
{
    public class Hamster
    {
        public int Id { get; }
        private readonly Timer _timer;

        public int Rounds
        {
            get;
            private set;
        }

        public event EventHandler NewWheelRevolution;

        public Hamster(int id)
        {
            Id = id;
            _timer = new Timer(NewRound);
        }

        public void Run(TimeSpan timeBetweenWheels)
        {
            _timer.Change(timeBetweenWheels, timeBetweenWheels);
        }

        private void NewRound(object? state)
        {
            Rounds++;
            NewWheelRevolution?.Invoke(this, EventArgs.Empty);
        }

        public void Stop()
        {
            _timer.Change(Timeout.Infinite, Timeout.Infinite);
        }

        public void Fondle(int amount)
        {
            Console.WriteLine($"{amount} fondles received for Hamster {Id}");
        }

        public void Punish(int amount)
        {
            Console.WriteLine($"{amount} punishments received for Hamster {Id}");
        }
    }
}
