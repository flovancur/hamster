#!/bin/bash
HMSTR=../bin/hamster
rm -f *.dat

okayesno()
{
    echo -n "OK? (j/n)"
    read ok
    if [ "$ok" \= "n" ]; then
        echo "Fehler in Test $1" >>protocol.txt
    fi
}


check_interactive()
{
	testno=$1
	expect=$2
	cmd=$3
	errmsg=$4
	received=`$cmd`
	echo "*********************************************"
	echo "** $testno Erwartet wird:"
	echo "$expect"
	echo "** $testno Erhalten wurde:"
	echo "$received"
	echo "*********************************************"
	okayesno "$testno $errmsg"
}

check_noninteractive()
{
	testno=$1
	expect=$2
	cmd=$3
	errmsg=$4
	received=`$cmd`
	echo "$received" >__received.txt
	echo "$expect">__expected.txt
	difference=`diff -b __received.txt __expected.txt`
	rm -f __received.txt __expected.txt	
	if [ "$difference" ] ; then
		echo "*********************************************"
		echo "** $testno: Descrepancies:"
		echo "$difference"
		echo "*********************************************"
		okayesno "$testno $errmsg"
	fi
}


check=check_interactive

for i in "$@"
do
case $i in
    -n|--noninteractive)
	check=check_noninteractive
    shift # past argument
    ;;
    -i|--interactive)
	check=check_interactive
    shift # past argument
    ;;
    --default)
    shift # past argument with no value
    ;;
    *)
          # unknown option
    ;;
esac
done


echo "*********************************************"
echo "**               Hamstertest               **"
echo "*********************************************"
sleep 1

echo "*********************************************"
echo "**       0    RTFM-Nachricht               **"
echo "*********************************************"
rtfm=`$HMSTR`
if [ -z "$rtfm" ] ; then
	echo "*********************************************"
	echo "** 0 Erwartet wird:"
	echo "RTFM-Nachricht"
	echo "** $testno Erhalten wurde:"
	echo "*NICHTS* -> Fatal"
	echo "*********************************************"
	okayesno "0 Fatal:No_RTFM"
	exit
fi

echo "*********************************************"
echo "**       1    Normale Benutzung            **"
echo "*********************************************"

$HMSTR add schmidt baggins 17
$HMSTR add mueller mueller 22
$HMSTR add meier meier

sleep 4

testno="1.1"
read -r -d '' expect <<'EOF'
Owner		Name	Price	treats left
schmidt	baggins	17 €	17
mueller	mueller	17 €	22
meier	meier	17 €	0
EOF
cmd="$HMSTR list"
$check "$testno" "$expect" "$cmd" "list Fehler"


testno="1.2"
read -r -d '' expect <<'EOF'
Owner		Name	Price	treats left
mueller	mueller	17 €	22
EOF
cmd="$HMSTR list mueller"
$check "$testno" "$expect" "$cmd" "list Name Fehler"


testno="1.3.1"
expect="Done! 14 treats remaining in store"
cmd="$HMSTR feed schmidt baggins 3"
$check "$testno" "$expect" "$cmd" "feed Fehler"

testno="1.3.2"
read -r -d '' expect <<'EOF'
Owner		Name	Price	treats left
schmidt	baggins	17 €	14
EOF
cmd="$HMSTR list schmidt"
$check "$testno" "$expect" "$cmd" "feed Fehler"


testno="1.4"
read -r -d '' expect <<'EOF'
mueller's hamster mueller has done > 0 hamster wheel revolutions,
and has 22 treats left in store. Current price is 18 €
EOF
cmd="$HMSTR state mueller mueller"
$check "$testno" "$expect" "$cmd" "state Fehler"

testno="1.5"
read -r -d '' expect <<'EOF'
meier has to pay 17 €
EOF
cmd="$HMSTR remove-all meier"
$check "$testno" "$expect" "$cmd" "remove-all Name Fehler"


testno="1.6"
read -r -d '' expect <<'EOF'
Owner		Name	Price	treats left
schmidt	baggins	17 €	14
mueller	mueller	18 €	22
EOF
cmd="$HMSTR list"
$check "$testno" "$expect" "$cmd" "list nach löschen -> Fehler"


echo "*********************************************"
echo "**       2    Falsche Kommandos            **"
echo "*********************************************"

sleep 1

testno="2.1"
expect="$rtfm"
cmd="$HMSTR"
$check "$testno" "$expect" "$cmd" "keine Option -> kein Fehler"


testno="2.2"
expect="$rtfm"
cmd="$HMSTR -u"
$check "$testno" "$expect" "$cmd" "ungueltige Option -> kein Fehler"


testno="2.3"
expect="$rtfm"

cmd="$HMSTR list blah blubb"
$check "$testno" "$expect" "$cmd" "list zuviele args -> kein Fehler"


testno="2.4"
expect="$rtfm"

cmd="$HMSTR add blah ratz 22 fatz"
$check "$testno" "$expect" "$cmd" "add zuviele args -> kein Fehler"


testno="2.5"
expect="$rtfm"
cmd="$HMSTR add blah"
$check "$testno" "$expect" "$cmd" "add zu wenige args -> kein Fehler"


testno="2.6"
expect="$rtfm"
cmd="$HMSTR add"
$check "$testno" "$expect" "$cmd" "add keine args -> kein Fehler"


testno="2.7"
expect="$HMSTR: Not a number: fatz"
cmd="$HMSTR add blah ratz fatz"
$check "$testno" "$expect" "$cmd" "add keine Zahl -> kein Fehler"


testno="2.8"
expect="$rtfm"
cmd="$HMSTR feed blah ratz 22 fatz"
$check "$testno" "$expect" "$cmd" "feed zuviele args -> kein Fehler"


testno="2.9"
expect="$rtfm"
cmd="$HMSTR feed blah ratz"
$check "$testno" "$expect" "$cmd" "feed zu wenige args -> kein Fehler"


testno="2.10"
expect="$rtfm"
cmd="$HMSTR feed blah"
$check "$testno" "$expect" "$cmd" "feed zu wenige args -> kein Fehler"


testno="2.11"
expect="$rtfm"
cmd="$HMSTR feed"
$check "$testno" "$expect" "$cmd" "feed keine args -> kein Fehler"


testno="2.12"
expect="$HMSTR: Not a number: fatz"
cmd="$HMSTR feed blah ratz fatz"
$check "$testno" "$expect" "$cmd" "feed keine Zahl -> kein Fehler"


testno="2.13"
expect="$rtfm"
cmd="$HMSTR state blah ratz fatz"
$check "$testno" "$expect" "$cmd" "state zuviele args -> kein Fehler"


testno="2.14"
expect="$rtfm"
cmd="$HMSTR state blah"
$check "$testno" "$expect" "$cmd" "state zu wenige args -> kein Fehler"


testno="2.15"
expect="$rtfm"
cmd="$HMSTR state"
$check "$testno" "$expect" "$cmd" "state keine args -> kein Fehler"


testno="2.16"
expect="$rtfm"
cmd="$HMSTR remove-all blah ratz"
$check "$testno" "$expect" "$cmd" "remove-all zuviele args -> kein Fehler"


testno="2.17"
expect="$rtfm"
cmd="$HMSTR remove-all"
$check "$testno" "$expect" "$cmd" "remove-all keine args -> kein Fehler"


echo "*********************************************"
echo "**      3    Datenbezogene Tests           **"
echo "*********************************************"

sleep 1

testno="3.1"
expect="No hamsters matching criteria found"
cmd="$HMSTR list motzki"
$check "$testno" "$expect" "$cmd" "list unbek Kunde -> kein Fehler"

testno="3.2.1"
expect="schmidt has to pay 17 €"
cmd="$HMSTR remove-all schmidt"
$check "$testno" "$expect" "$cmd" "remove-all Fehler"


testno="3.2.2"
expect="mueller has to pay 18 €"
cmd="$HMSTR remove-all mueller"
$check "$testno" "$expect" "$cmd" "remove-all Fehler"

testno="3.2.3"
expect="No hamsters matching criteria found"
cmd="$HMSTR list"
$check "$testno" "$expect" "$cmd" "list keine Hamster, trotzdem Anzeige"

testno="3.3.1" 
expect="Done!"
cmd="$HMSTR add Paus Backenbube"
$check "$testno" "$expect" "$cmd" "add Fehler"

testno="3.3.2" 
expect="Error: a hamster by that owner/name already exists "
cmd="$HMSTR add Paus Backenbube"
$check "$testno" "$expect" "$cmd" "add Hamster schon da -> kein Fehler"

testno="3.4.1"
expect="Done!"
cmd="$HMSTR add meier meier"
$check "$testno" "$expect" "$cmd" "add geht nicht"

testno="3.4.2"
expect="Done!"
cmd="$HMSTR add mueller mueller"
$check "$testno" "$expect" "$cmd" "add geht nicht"

testno="3.5.1"
expect="Error: a hamster by that owner/name already exists"
cmd="$HMSTR add meier meier"
$check "$testno" "$expect" "$cmd" "add Hamster schon da -> kein Fehler"

testno="3.5.2"
expect="Error: a hamster by that owner/name already exists"
cmd="$HMSTR add mueller mueller"
$check "$testno" "$expect" "$cmd" "add Hamster schon da -> kein Fehler"

testno="3.6" 
expect="Error: A hamster or hamster owner could not be found"
cmd="$HMSTR feed Blott OnTheLandscape 222"
$check "$testno" "$expect" "$cmd" "feed unbek Kunde -> kein Fehler"


testno="3.7 Erwartet wird: "
expect="Error: A hamster or hamster owner could not be found"
cmd="$HMSTR state Blott OnTheLandscape"
$check "$testno" "$expect" "$cmd" "state unbek Kunde -> kein Fehler"

testno="3.8"
expect="Error: A hamster or hamster owner could not be found"
cmd="$HMSTR remove-all Blott"
$check "$testno" "$expect" "$cmd" "remove-all unbek Kunde -> kein Fehler"


echo "*********************************************"
echo "**      4   zu lange Namen                 **"
echo "*********************************************"

sleep 1

testno="4.1"
expect="Error: the specified name is too long"
cmd="$HMSTR list Ludovic_Freihherr_von_Knoblauch_zu_Hatzbach"
$check "$testno" "$expect" "$cmd" "list Langer Name -> kein Fehler"


testno="4.2"
expect="Error: the specified name is too long"
cmd="$HMSTR add Ludovic_Freihherr_von_Knoblauch_zu_Hatzbach Paus_Backenbube_von_Knoblauch_zu_Hatzbach"
$check "$testno" "$expect" "$cmd" "add zwei Lange Namen -> kein Fehler"


testno="4.3"
expect="Error: the specified name is too long"
cmd="$HMSTR add Ludovic_Freihherr_von_Knoblauch_zu_Hatzbach Paus_Backenbube"
$check "$testno" "$expect" "$cmd" "add ein Langer Name -> kein Fehler"


testno="4.4"
expect="Error: the specified name is too long"
cmd="$HMSTR add Ludo Paus_Backenbube_von_Knoblauch_zu_Hatzbach"
$check "$testno" "$expect" "$cmd" "add ein Langer Name -> kein Fehler"


testno="4.5"
expect="Error: the specified name is too long"
cmd="$HMSTR feed Ludovic_Freihherr_von_Knoblauch_zu_Hatzbach Paus_Backenbube_von_Knoblauch_zu_Hatzbach 234"
$check "$testno" "$expect" "$cmd" "feed zwei Lange Namen -> kein Fehler"

testno="4.5"
expect="Error: the specified name is too long"
cmd="$HMSTR feed Ludovic_Freihherr_von_Knoblauch_zu_Hatzbach Paus_Backenbube 567"
$check "$testno" "$expect" "$cmd" "feed ein Langer Name -> kein Fehler"

testno="4.6"
expect="Error: the specified name is too long"
cmd="$HMSTR feed Ludo Paus_Backenbube_von_Knoblauch_zu_Hatzbach 789"
$check "$testno" "$expect" "$cmd" "feed ein Langer Name -> kein Fehler"

testno="4.7"
expect="Error: the specified name is too long"
cmd="$HMSTR state Ludovic_Freihherr_von_Knoblauch_zu_Hatzbach Paus_Backenbube_von_Knoblauch_zu_Hatzbach"
$check "$testno" "$expect" "$cmd" "state zwei Lange Namen -> kein Fehler"

testno="4.7"
expect="Error: the specified name is too long"
cmd="$HMSTR state Ludovic_Freihherr_von_Knoblauch_zu_Hatzbach Paus_Backenbube"
$check "$testno" "$expect" "$cmd" "state ein Langer Name -> kein Fehler"

testno="4.8"
expect="Error: the specified name is too long"
cmd="$HMSTR state Ludo Paus_Backenbube_von_Knoblauch_zu_Hatzbach"
$check "$testno" "$expect" "$cmd" "state ein Langer Name -> kein Fehler"

testno="4.9"
expect="Error: the specified name is too long"
cmd="$HMSTR remove-all Ludovic_Freihherr_von_Knoblauch_zu_Hatzbach"
$check "$testno" "$expect" "$cmd" "remove-all Langer Name -> kein Fehler"
