import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;

/**
 * Client program.  Connects to the server and sends text accross.
 */

public class Client 
{
    private Socket sock;  //Socket to communicate with.
	private byte[] keySeed; // Seed for RNG key
	private static boolean debugFlag;
	private long fileLength;
	private String destName;
    /**
     * Main method, starts the client.
     * @param args args[0] needs to be a hostname, args[1] a port number.
     */
    public static void main (String [] args)
    {
		System.out.println(args.length);
	if (args.length < 2 || args.length > 3) {
	    System.out.println ("Usage: java Client hostname port#");
	    System.out.println ("hostname is a string identifying your server");
	    System.out.println ("port is a positive integer identifying the port to connect to the server");
	    return;
	}

	if(args.length == 3)
		if(args[2].compareTo("debug") == 0){
			debugFlag = true;
		System.out.println("Debug mode running, sensitive info WILL be output to screen");}

	try {

	    Client c = new Client (args[0], Integer.parseInt(args[1]));
	}
	catch (NumberFormatException e){
	    System.out.println ("Usage: java Client hostname port#");
	    System.out.println ("Second argument was not a port number");
	    return;
	}
    }
	
    /**
     * Constructor, in this case does everything.
     * @param ipaddress The hostname to connect to.
     * @param port The port to connect to.
     */
    public Client (String ipaddress, int port)
    {
	/* Allows us to get input from the keyboard. */
	BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader fromServer;
	String userinput;
	PrintWriter out;
		OutputStream fileOut;
		
	/* Try to connect to the specified host on the specified port. */
	try {
	    sock = new Socket (InetAddress.getByName(ipaddress), port);
	}
	catch (UnknownHostException e) {
	    System.out.println ("Usage: java Client hostname port#");
	    System.out.println ("First argument is not a valid hostname");
	    return;
	}
	catch (IOException e) {
	    System.out.println ("Could not connect to " + ipaddress + ".");
	    return;
	}
		
	/* Status info */
	System.out.println ("Connected to " + sock.getInetAddress().getHostAddress() + " on port " + port);
		
	try {
	    out = new PrintWriter(sock.getOutputStream());
		fileOut = sock.getOutputStream();
		fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	}
	catch (IOException e) {
	    System.out.println ("Could not create output stream.");
	    return;
	}
		
	/* Wait for the user to type stuff. */
	try {
		System.out.println("At any time, type \"exit\" or \"die\" to disconnect");
		System.out.print("Please provide your seed: ");
		String stringKey = stdIn.readLine();
		keySeed = stringKey.getBytes();
		if(debugFlag)
			System.out.println("DEBUG: Seed \"" +stringKey + "\" has been provided.");
		System.out.println("You're good to go! Type the filename you wish to transfer and press ENTER");


	    while ((userinput = stdIn.readLine()) != null) {
		/* Echo it to the screen. */
		System.out.println(userinput);
			    
		/* Tricky bit.  Since Java does short circuiting of logical 
		 * expressions, we need to checkerror to be first so it is always 
		 * executes.  Check error flushes the outputstream, which we need
		 * to do every time after the user types something, otherwise, 
		 * Java will wait for the send buffer to fill up before actually 
		 * sending anything.  See PrintWriter.flush().  If checkerror
		 * has reported an error, that means the last packet was not 
		 * delivered and the server has disconnected, probably because 
		 * another client has told it to shutdown.  Then we check to see
		 * if the user has exitted or asked the server to shutdown.  In 
		 * any of these cases we close our streams and exit.
		 */
		if ((out.checkError()) || (userinput.compareTo("exit") == 0) || (userinput.compareTo("die") == 0)) {
		    System.out.println ("Client exiting.");
		    stdIn.close ();
		    out.close ();
		    sock.close();
		    return;
		}

		else{
			// User input = filename
			String fileName = userinput;
			File file = new File(userinput); // Read from file
			if(debugFlag)
				System.out.println("DEBUG: File: " + file.getName() + " provided");
			System.out.println("Enter a destination file name: ");
			destName = stdIn.readLine();
			if(debugFlag)
				System.out.println("DEBUG: Destination file: " + destName);



			out.println("begin");


			out.println(destName);  //Filename
			out.flush(); // Send to server


			if(debugFlag)
				System.out.println("File encryption started");
			// Transfer file
			secureFile.main(fileName, fileOut, keySeed, debugFlag);
			out.flush(); // Send encrypted file to server


			if(debugFlag)
				System.out.println("File sent to server... Awaiting response.");

			String response = fromServer.readLine();

			if(response.compareTo("success") == 0)
				System.out.println("File transfer successful. Exiting client.");
			else {
				System.out.println("Something went wrong server side. Please try again by restarting client.");
				if(debugFlag)
					System.out.println("DEBUG: Response(" + response + ")");
			}


		}



	    }
	} catch (IOException e) {
	    System.out.println ("Could not read from input.");
	    return;
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
}
