# Assignment 3 - Diffie Hellman Secure File Transfer
Java application to generate a DH (Diffie Hellman) key pair between a client and server. Allowing the client to encrypt and transfer a file to the remote server. Includes both applications for server and client side functionality.

When calling the program from Java command line, the debug argument is not necessary for function, only for debugging purposes. It should also be noted that sensitive information is output to the console when in debug mode, use of this mode for purposes further than debugging is not advised.

Hash function used for generating file digest: HMAC-SHA1

Algorithm used for encrypting file: AES-128-CBC

Key Generator: Diffie Hellman Key Agreement Protocol (gab)

## Files Included
- Client.java Contains main method used to generate a DH key pair with the Server. After doing so the Client can then be used to transfer a file to the remote server. Takes 3 command line arguments, called in the form: Java Client <servername> <port> <debug>

- Server.java Contains main method for Sever side protocol. Generates a DH key pair separately for each Client that connects. Takes 1-2 command line arguments, called in the form: Java Server <port> <debug>

- CyptoUtilities.java Contains methods used for encryption, decryption, and hash calculation. All methods are implemented elsewhere, and usage of this file isn’t required for user functionality.

- ServerThread.java Bulk of the code server side that deals with receiving messages and files from a specific client. Called from Server.java.

## Usage
Making the Program: Call make from a Unix shell and the provided makefile will ensure that all proper files are compiled for use.

Creating a Server: Call from command line “java Server <port>”
- On success, program will wait for a client to connect
- Once a client connects, the DH key pair is generated and made common between the two protocols. Used for encryption for the rest of the protocol.
- On failure, program will output error message and kill the server
- Can also debug by including “debug” as a 2nd argument

Creating a Client connection: Call from command line “java Client <hostname> <port>
- On success, program will connect to given server, and output success message to screen
- Once a client is connected, the server and client will automatically generate a DH key pair for encryption.
- If unsuccessful client will report and error and no connection will be established
- Can also debug by including “debug” as a 2nd argument

Sending a file: Once Client is running and connected, follow steps provided in terminal for successful file transfer
- On success, file will be securely transferred and saved on the server
- If unsuccessful client will receive the error and connection will be closed.
#### Tested on Windows and Undergraduate Linux CPSC Server
## To-Do
Implementation is complete, no to-do’s.

## Bugs
No known bugs exist in this release.

## Additional Credit
Credit to provided provided source, and provided Client, Server, ServerThread, and CryptoUtilities.
## Key Exchange Protocol
This section is intended for those interested if further understanding the underlying math/computation of the DH key pair. All arithmetic is done using Java’s BigInteger class to handle very large numbers. Random generation is done using SecureRandom. Broken into steps, the algorithm used is:
1) Server generates a large prime q using SecureRandom RNG and 1023 bit size via BigInteger’s generation constructor. Then computes p = 2q+1

  a. If p is not significantly prime (Showing prime after multiple factor tests) then repeat 1)

2) Once p is established, the server then sends p to the client

3) Compute g by starting at g = 1, and computing gq (mod p)

  a. If the result of the exponentiation and mod is congruent to 1, increment g and try again

  b. This usually only takes around 7 attempts.

4) Once g is established, the server then sends g to the client

5) The server now generates some random b and computes gb (mod p)

  a. b is generated randomly of the same bit size as q
  
  b. Since b is required to be at most p-2 the generated number is b (mod p-2) to ensure this is fulfilled
  
  c. The finding of b starts at 3 instead of 1, to help combat the issue with b=2 a high percentage of the time
  
6) Once gb (mod p) is established, the server sends the value as the partial key to the client

7) The client now holds p, g, and gb (mod p). Using these, the client now computes an a the same way the server computed b.

  a. a is generated randomly of the same bit size as q
  
  b. Since a is required to be at most p-2 the generated number is a (mod p-2) to ensure this is fulfilled
  
8) The client also computes at this time (gb )a(mod p) using the value received from the server and the a that was just found.

9) The client now sends back ga (mod p) to the server, followed by (gb )a(mod p)

10) The server receives ga (mod p) and computes (ga )b(mod p) and compares this to the second value received from the client ((gb )a(mod p))

  a. If they are not equal, then they must have been altered, and will no longer be accepted by the protocol.
  
11) Finally the server sends back the value that it computed (ga )b(mod p) and the client compares it to their key ((gb )a(mod p)). Doing so ensures that there is no eavesdropper on the channel changing values.
