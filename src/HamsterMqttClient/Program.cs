using CommandLine;
using CommandLine.Text;
using HamsterMqttClient;

var verbs = new Type[]
{
    typeof(EatVerb),
    typeof(MateVerb),
    typeof(MoveVerb),
    typeof(RunVerb),
    typeof(SleepVerb),
    typeof(QuitVerb)
};

Parser.Default.ParseArguments<HamsterOptions>(args)
    .WithParsedAsync(async options =>
    {
        if (!options.Silent)
        {
            Console.WriteLine(new HelpText("This is an interactive client, you can use the following commands:", string.Empty)
                .AddVerbs(verbs)
                .ToString());
        }
        var hamster = new Hamster(options.HamsterId);
        var client = new HamsterClient(hamster);
        await client.Connect(options);
        while(true)
        {
            Console.Write("> ");
            var line = Console.ReadLine();
            if (!await Parser.Default
                    .ParseArguments(line.SplitArgs(),verbs)
                    .MapResult(
                        (VerbBase verb) => verb.TryRun(client), 
                        _ => Task.FromResult(true)
                     ))
            {
                break;
            }
        }
        await client.Disconnect();
    }).Wait();