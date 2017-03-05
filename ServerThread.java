import javax.xml.crypto.Data;
import java.net.*;
import java.io.*;

/**
 * Thread to deal with clients who connect to Server.  Put what you want the
 * thread to do in it's run() method.
 */

public class ServerThread extends Thread
{
    private Socket sock;  //The socket it communicates with the client on.
    private Server parent;  //Reference to Server object for message passing.
    private int idnum;  //The client's id number.
	private boolean debugFlag;
	byte[] seed;
	
    /**
     * Constructor, does the usual stuff.
     * @param s Communication Socket.
     * @param p Reference to parent thread.
     * @param id ID Number.
     */
    public ServerThread (Socket s, Server p, int id, boolean debugFlag, byte[] seed)
    {
	parent = p;
	sock = s;
	idnum = id;
		this.debugFlag = debugFlag;
		this.seed = seed;
    }
	
    /**
     * Getter for id number.
     * @return ID Number
     */
    public int getID ()
    {
	return idnum;
    }
	
    /**
     * Getter for the socket, this way the parent thread can
     * access the socket and close it, causing the thread to
     * stop blocking on IO operations and see that the server's
     * shutdown flag is true and terminate.
     * @return The Socket.
     */
    public Socket getSocket ()
    {
	return sock;
    }
	
    /**
     * This is what the thread does as it executes.  Listens on the socket
     * for incoming data and then echos it to the screen.  A client can also
     * ask to be disconnected with "exit" or to shutdown the server with "die".
     */
    public void run ()
    {
	BufferedReader in = null;
	DataInputStream fileIn = null;
	String incoming = null;
		PrintWriter out = null;
		String response;
		
	try {
	    in = new BufferedReader (new InputStreamReader (sock.getInputStream()));
		fileIn = new DataInputStream(sock.getInputStream());
		out = new PrintWriter(sock.getOutputStream());
	}
	catch (UnknownHostException e) {
	    System.out.println ("Unknown host error.");
	    return;
	}
	catch (IOException e) {
	    System.out.println ("Could not establish communication.");
	    return;
	}
		
	/* Try to read from the socket */
	try {
	    incoming = in.readLine ();
	}
	catch (IOException e) {
	    if (parent.getFlag())
		{
		    System.out.println ("shutting down.");
		    return;
		}
	    return;
	}
		
	/* See if we've recieved something */
	while (incoming != null)
	    {
		/* If the client has sent "exit", instruct the server to
		 * remove this thread from the vector of active connections.
		 * Then close the socket and exit.
		 */
		if (incoming.compareTo("exit") == 0)
		    {
			parent.kill (this);
			try {
			    in.close ();
			    sock.close ();
			}
			catch (IOException e)
			    {/*nothing to do*/}
			return;
		    }
			
		/* If the client has sent "die", instruct the server to
		 * signal all threads to shutdown, then exit.
		 */
		else if (incoming.compareTo("die") == 0)
		    {
			parent.killall ();
			return;
		    }	
			
		/* Otherwise, just echo what was recieved. */
		System.out.println ("Client " + idnum + ": " + incoming);
			
		/* Try to get the next line.  If an IOException occurs it is
		 * probably because another client told the server to shutdown,
		 * the server has closed this thread's socket and is signalling
		 * for the thread to shutdown using the shutdown flag.
		 */
		try {

			if (incoming.compareTo("begin") == 0){
				if(debugFlag)
					System.out.println("DEBUG: Beginning file read");
				// Read the file parts
				String fileName = in.readLine();
				if(debugFlag)
					System.out.println("DEBUG: Filename: " + fileName);
				int fileLength = Integer.parseInt(in.readLine());
				if(debugFlag)
					System.out.println("DEBUG: File length: " + fileLength);

				System.out.println("Getting file from client...");

				byte[] data = new byte[fileLength];
				if(fileLength > 0) {
					fileIn.readFully(data);

				}

				response = "success";

				try {
					decryptFile.main(data, seed, fileName); // Decrypts and saves file accordingly
				}
				catch(Exception e){
					response = e.toString();
				}
				System.out.println("File successfully saved.");
				if(debugFlag)
					System.out.println("DEBUG: Filename: " + fileName);

				out.println(response);
				out.flush();

				incoming = null; // Breaks out of next loop
			}

		}
		catch (IOException e) {
		    if (parent.getFlag())
			{
			    System.out.println ("shutting down.");
			    return;
			}
		    else
			{
			    System.out.println ("IO Error.");
			    return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
    }
}
