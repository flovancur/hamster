# HamsterRPC

Das selbst gestrickte RPC-Protokoll der Open-Source Bibliothek Hamsterlib, 
ist zwar robust und effizient, jedoch ist die Implementierung komplex und 
aufwändig. Das IT-Unternehmen, welches die aktuelle Implementierung des 
RPC-Protokolls vertreibt, verlangt horrende Preise für die Wartung. Um 
auf lange Sicht Geld zu sparen, beschließt das westhessische 
Hamsterverwahrungsunternehmen in eine standardisierte RPC-Lösung zu 
investieren und somit nicht mehr von einem Unternehmen abhängig zu sein.
Das gRPC Protokoll ist allgemein verfügbar, es ist für gute Effizienz bekannt
und es erlaubt durch die Verfügbarkeit von Implementierungen in verschiedenen Programmiersprachen und -plattformen problemlos die Weiterverwendung
der Hamsterbibliothek. Deshalb entscheidet man sich für diesen Standard.

Unter *src* finden Sie den Quellcode eines einfachen Menüprogramms, das die API der
Hamsterlib verwendet.

Ihre Aufgabe ist es nun, dieses Programm in ein verteiltes Programm,
bestehend aus einem *Server* und einem *Client*, umzuwandeln. Dabei sollen
Server und Client mittels gRPC miteinander kommunizieren.

Dazu sind folgende Schritte erforderlich:

- Erstellen einer RPC *Schnittstellenspezifikation* `hamster.proto`
	im Unterverzeichnis `proto`.
- Automatisches Generieren der *RPC stubs*. Details hierzu finden Sie in den technologiespezifischen Hinweisen.
- Manuelles Anpassen der client- und serverseitigen Templates so, dass der Server
	die Schnittstelle der Hamsterlib aufruft und der Client eine zu dieser
	Schittstelle identische, auf RPC aufgesetzte Schnittstelle bietet.
- Kompilieren der client- und serverseitigen Programmteile

## Tests

Die Testsuite für diese Aufgabe ist dieselbe wie beim letzten Übungsblatt, allerdings wird der Testtreiber nun
Ihren Client aufrufen, anstatt wie beim letzten mal direkt als Client zu agieren.

Sie können die Testsuite wie gehabt als ausführbares Jar-Archiv ausführen, auch die Ausgaben sind identisch zum letzten Übungsblatt.

## Tipps zur Bearbeitung

Machen Sie sich zunächst anhand eines einfachen Beispiels mit der Benutzung
von gRPC vertraut. Es gibt unzählige gRPC Tutorials im Netz. Mit [1](https://grpc.io/docs/languages/java/basics/) sei
hier nur eines davon genannt. Erproben Sie das dort gezeigte Beispiel, um die
Benutzung der Tools kennenzulernen.

## Hinweise Java

In Gradle sind nun zwei Build targets hinterlegt, einmal das Ziel `hamster_server_jar` und
einmal das Ziel `hamster_client_jar`. Beachten Sie bitte, dass das in den Vorlagen enthaltene
Skript gradle-build.sh immer nur ein Ziel bauen kann, falls Sie also auf einem Poolrechner
arbeiten, sollten Sie immer nur ein Target auf einmal bauen oder direkt mit gradlew arbeiten
und den Proxy direkt spezifizieren (umständlich).

In Gradle sind Plugins hinterlegt, die automatisch aus Ihren Proto-Dateien Code generieren
und diesen den gängigen Entwicklungsumgebungen zugänglich machen. Das können allerdings
nicht alle Entwicklungsumgebungen, daher auch hier der Hinweis auf IntelliJ. Es empfiehlt sich
also, erst die Proto-Datei zu bearbeiten und dann zu kompilieren, damit der Quellcode aus der
Proto-Datei generiert wird und Ihnen dann zur Verfügung steht.