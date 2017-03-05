
default: secureFile.class decryptFile.class DigestMe.class MessageAlteredException.class

secureFile.class: secureFile.java
	javac -g secureFile.java

decryptFile.class: decryptFile.java
	javac -g decryptFile.java

DigestMe.class: DigestMe.java
	javac -g DigestMe.java

MessageAlteredException.class: MessageAlteredException.java
	javac -g MessageAlteredException.java
