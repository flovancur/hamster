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
