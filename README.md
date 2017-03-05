# Assignemnt 2 - Encrypted File Transfer
Java application to encrypt and transfer a file to a remote server. Includes both applications for server and client side functionality.
Hash function used for generating file digest: HMAC-SHA1
Algorithm used for encrypting file: AES-128-CBC
Key Generator: Given Seed -> SHA-1 Digest -> Key
## Files Included
  - secureFile.java Contains main method used for loading encrypting a file and calculating the hash of the file. Takes 4 arguments, of type: String filename, OutputStream out, Byte[] seed, Boolean debugflag Returns nothing but sends encrypted byte array through OutputStream provided.
  - decryptFile.java Contains main method used for decrypting a byte array received from the server and verifying the hash of the file. Takes 4 arguments, in the form: Byte[] file, Byte[] seed, String outName. Returns nothing, but writes to the filename “outName”. Will break out if hash doesn’t match expected hash.
  - CyptoUtilities.java Contains methods used for encryption, decryption, and hash calculation. All methods are implemented elsewhere, and usage of this file isn’t required for user functionality.
  - Client.java Contains the client side architecture. Takes in 2-3 command line arguments in the form: java Client <hostname> <port> <debug> Where “<debug>” can be toggled on by typing debug, off if nothing is typed. Deals with connecting to the server, sending information about the file, and preparing to send encrypted file. Outputs messages to user to ensure transmission is successful.
  - Server.java Contains the Server side architecture used to create a separate thread for a Client connection. Called with 1-2 command line arguments in the form: java Server <port> <debug> Where “<debug>” can be toggled on by typing debug, off if nothing is typed.
  - ServerThread.java Bulk of the code server side that deals with receiving messages and files from a specific client. Called from Server.java.
  
## Usage
### Creating a Server: Call from command line “java Server <port>”
- On success, program will wait for a client to connect
- On failure, program will output error message and kill the server
- Can also debug by including “debug” as a 2nd argument
### Creating a Client connection: Call from command line “java Client <hostname> <port>
- On success, program will connect to given server, and output success message to screen
- If unsuccessful client will report and error and no connection will be established
- Can also debug by including “debug” as a 2nd argument
### Sending a file: Once Client is running and connected, follow steps provided in terminal for successful file transfer
- On success, file will be transferred and saved on the server
- If unsuccessful client will receive the error and connection will be closed.
Tested on Windows & Undergraduate Linux CPSC Server
## To-Do
Implementation is complete, no to-do’s.
## Bugs
No known bugs exist in this release.
## Additional Credit
Credit to provided provided source, and provided secure and decrypt utilities.
