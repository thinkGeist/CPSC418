
default: secureFile.class decryptFile.class Server.class ServerThread.class Client.class CryptoUtilities.class

secureFile.class: secureFile.java
	javac -g secureFile.java

decryptFile.class: decryptFile.java
	javac -g decryptFile.java

Server.class: Server.java
	javac -g Server.java

ServerThread.class: ServerThread.java
	javac -g ServerThread.java

Client.class: Client.java
	javac -g Client.java

CryptoUtilities.class: CryptoUtilities.java
	javac -g CryptoUtilities.java
