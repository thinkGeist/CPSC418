import java.io.*;
import javax.crypto.spec.*;

/**
 * This program performs the following cryptographic operations on the input file:
 *   - computes a random 128-bit key (1st 16 bits of SHA-1 hash of a user-supplied seed)
 *   - computes a HMAC-SHA1 hash of the file's contents
 *   - encrypts the file+hash using AES-128-CBC
 *   - outputs the encrypted data
 *
 * Compilation:    javac secureFile.java
 * Execution: java secureFile [plaintext-filename] [ciphertext-filename] [seed]
 *
 * @author Mike Jacobson
 * @version 1.0, September 25, 2013
 */
public class secureFile{

    public static void main(String fileName, OutputStream outStream, byte[] seed, boolean debugFlag) throws Exception{
	FileInputStream in_file = null;
	try{
	    // open input and output files
		PrintWriter out = new PrintWriter(outStream);
	    in_file = new FileInputStream(fileName);

	    // read input file into a byte array
	    byte[] msg = new byte[in_file.available()];
	    int read_bytes = in_file.read(msg);

	    // compute key:  1st 16 bytes of SHA-1 hash of seed
	    SecretKeySpec key = CryptoUtilities.key_from_seed(seed);

	    // append HMAC-SHA-1 message digest
	    byte[] hashed_msg = CryptoUtilities.append_hash(msg,key);

	    // do AES encryption
	    byte[] aes_ciphertext = CryptoUtilities.encrypt(hashed_msg,key);
		out.println(aes_ciphertext.length);
		if(debugFlag)
			System.out.println("DEBUG: Length of file to be transferred: " + aes_ciphertext.length + " bytes");
		out.flush();
	    // output the ciphertext
		int total = aes_ciphertext.length;
		sendBytes(aes_ciphertext, outStream);




	}
	catch(Exception e){
	    System.out.println(e);
	}
	finally{
	    if (in_file != null){
		in_file.close();
	    }
	}
    }
	public static void sendBytes(byte[] myByteArray, OutputStream outStream) throws IOException {
		sendBytes(myByteArray, 0, myByteArray.length, outStream);
	}

	public static void sendBytes(byte[] myByteArray, int start, int len, OutputStream outStream) throws IOException {
		if (len < 0)
			throw new IllegalArgumentException("Negative length not allowed");
		if (start < 0 || start >= myByteArray.length)
			throw new IndexOutOfBoundsException("Out of bounds: " + start);

		// Thrown exceptions should never be reached on proper running of the code. Indicates error at runtime and is handled as such

		DataOutputStream dos = new DataOutputStream(outStream);
		if (len > 0) {
			dos.write(myByteArray, start, len); // Writes to the socket stream
		}
	}

}