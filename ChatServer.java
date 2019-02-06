
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

//thread import
import java.lang.Thread;
import java.util.ArrayList;

/**
 * Tuhina Dasgupta
 */
public class ChatServer extends ChatWindow{

	//creates an array list for the connected clients
	private ArrayList<ClientHandler> arrList = new ArrayList<>();

	public ChatServer()
	{
		super();
		this.setTitle("Chat Server");
		this.setLocation(80,80);

		try {
			// create a listening service for connections at the designated port number.
			ServerSocket server = new ServerSocket(2113);

			while (true)
			{
				// The method accept blocks until a client connects.
				printMsg("Waiting for a connection");
				Socket socket = server.accept();
				ClientHandler handler = new ClientHandler(socket);
				handler.connect();
				ClientHandler newClient =  new ClientHandler(socket);
				arrList.add(newClient);
			}
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}

	//inner class that handles client communication
	class ClientHandler implements Runnable {
		private PrintWriter writer;
		private BufferedReader reader;
		String clientName = "Client Sent: ";

		public ClientHandler(Socket socket) {
			try
			{
				InetAddress serverIP = socket.getInetAddress();
				printMsg("Connection made to " + serverIP);
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			catch (IOException e)
			{
				printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
		}
		public void handleConnection() {
			try {
				while(true)
				{
					// read a message from the client & sends message back
					String s = readMsg();
					sendMsg(s);
				}
			}
			catch (IOException e){
				printMsg("\nERROR Server:" + e.getLocalizedMessage() + "\n");
			}
		}

		//read and display message
		public String readMsg() throws IOException {

			String s = reader.readLine();
			String check = "";
			String newName = "";

			if(s.length() >= 7)
			{
				check = s.substring(0,5);
				newName = s.substring(6);
			}
			if(check.equals("/name"))
			{
				clientName = newName + ": ";
				printMsg("Client's New Name Is " + newName);
				s = "Client's New Name Is " + newName;
			}
			else{
				printMsg(clientName + s);
			}

			return s;
		}
		//send a string
		public void sendMsg(String s)
		{
			for(int i = 0; i<arrList.size(); i++) {
				arrList.get(i).writer.println(clientName + s);
			}

		}
		//runnable methods
		public void run()
		{
			this.handleConnection();
		}
		public void connect()
		{
			Thread thread = new Thread(this);
			thread.start();
		}
	}

	public static void main(String args[]){
		new ChatServer();
	}
}
