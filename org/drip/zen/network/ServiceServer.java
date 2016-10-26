
package org.drip.zen.network;

/*
 * 1) Server Class Abstraction Purpose (notion of a State Machine)
 * 2) Port Number Field / Instance Field Conventions
 * 3) Constructor
 * 4) Coding Conventions
 * 5) "final" Keyword
 * 6) Full Package Names
 * 7) Exception's - Introduction and Usage
 * 8) Class Instance Member Access
 */

public class ServiceServer {
	private int _iListenerPort = -1;
	private java.net.ServerSocket _ssListener = null;

	public ServiceServer (
		final int iListenerPort)
		throws java.lang.Exception
	{
		_ssListener = new java.net.ServerSocket (_iListenerPort = iListenerPort);
	}

	public int listenerPort()
	{
		return _iListenerPort;
	}

	public boolean processRequest()
	{
		try {
			java.net.Socket s = _ssListener.accept();

			System.out.println ("[Server] => Received a Connection from Client " + s);

			java.io.InputStream inputStream = s.getInputStream();

			java.io.InputStreamReader inputReader = new java.io.InputStreamReader (inputStream);

			java.io.BufferedReader bufferedReader = new java.io.BufferedReader (inputReader);

			java.lang.String request = bufferedReader.readLine();

	    	System.out.println ("[Server] => " + request);

	    	java.io.OutputStream outputStream = s.getOutputStream();

	    	java.io.PrintWriter pw = new java.io.PrintWriter (outputStream, true);

	    	pw.write ("I am OK - looks like our sockets talked to each other\n");

	    	pw.flush();

	    	return true;
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}

    	return false;
	}

	public static void main (
		final java.lang.String[] astrInput)
		throws java.lang.Exception
	{
		int listenerPort = 9090;

		ServiceServer ss = new ServiceServer (listenerPort);

		while (true)
			ss.processRequest();
	}
}
