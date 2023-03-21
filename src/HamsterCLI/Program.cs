ShowRtfm();

void ShowRtfm()
{
    Console.WriteLine("Usage: hamster {<Option>} <param1> {<param2>}");
    Console.WriteLine("Function: Hamster management");
    Console.WriteLine("Verbs:");
    Console.WriteLine("     list {<owner>}                   - show current list of hamsters");
    Console.WriteLine("     add <owner> <hamster> [<treats>] - add new hamster");
    Console.WriteLine("     feed <owner> <hamster> <treats>  - feed treats to hamster");
    Console.WriteLine("     state <owner> <hamster>          - how is my hamster doing?");
    Console.WriteLine("     bill <owner>                     - the bill please!");
}