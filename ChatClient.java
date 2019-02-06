
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Tuhina Dasgupta
 */
public class ChatClient extends ChatWindow //implements Runnable?
{

	// Inner class used for networking
	private Communicator comm;

	// GUI Objects
	private JTextField serverText;
	private JTextField nameText;
	private JButton connectButton;
	private JTextField messageText;
	private JButton sendButton;

	//sets up display of client window
	public ChatClient()
	{
		super();
		this.setTitle("Chat Client");
		printMsg("Chat Client Started.");

		// GUI elements at top of window
		// need a panel to store several buttons/text fields
		serverText = new JTextField("localhost");
		serverText.setColumns(15);
		nameText = new JTextField("Name");
		nameText.setColumns(10);
		connectButton = new JButton("Connect");
		JPanel topPanel = new JPanel();
		topPanel.add(serverText);
		topPanel.add(nameText);
		topPanel.add(connectButton);
		contentPane.add(topPanel, BorderLayout.NORTH);

		// GUI elements and panel at bottom of window
		messageText = new JTextField("");
		messageText.setColumns(40);
		sendButton = new JButton("Send");
		JPanel botPanel = new JPanel();
		botPanel.add(messageText);
		botPanel.add(sendButton);
		contentPane.add(botPanel, BorderLayout.SOUTH);

		// resize window to fit all GUI components
		this.pack();

		// setup the communicator so it will handle the connect client button
		Communicator comm = new Communicator();
		connectButton.addActionListener(comm);
		sendButton.addActionListener(comm);

	}

	//communicator communicates server
	class Communicator implements ActionListener, Runnable
	{
		private Socket socket;
		private PrintWriter writer;
		private BufferedReader reader;
		private int port = 2113;
		private boolean connected = false; //to verify user has connected before they try to send a message


		//if button clicked, according action performed
		@Override
		public void actionPerformed(ActionEvent actionEvent) {

			if (actionEvent.getActionCommand().compareTo("Connect") == 0) {
				connectClient();
				connected =true;

			}
			else if (actionEvent.getActionCommand().compareTo("Send") == 0)
			{
				if (connected) {
					sendMsg(messageText.getText());
				}
			}
		}

		// setup input and output streams
		public void connectClient()
		{
			try {
				socket = new Socket(serverText.getText(), port);
				InetAddress serverIP = socket.getInetAddress();
				printMsg("Connection made to " + serverIP);
				writer = new PrintWriter(socket.getOutputStream(), true);
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				sendMsg("Hello server");
				readMsg();
				connect();
			}
			catch(IOException e)
			{
				printMsg("\nERROR:" + e.getLocalizedMessage() + "\n");
			}
		}

		//read and display message
		public String readMsg() throws IOException
		{
			String s = reader.readLine();
			printMsg(s);
			return s;
		}

		//send a string
		public void sendMsg(String s){
			writer.println(s);
			messageText.setText(""); //clears send text from text box
		}

		//runnable methods
		public void run()
		{
			while(true)
			{
				try{
					readMsg();
				}
				catch(IOException e){

				}

			}
		}
		public void connect()
		{
			Thread thread = new Thread(this);
			thread.start();
		}

	}

	public static void main(String args[])
	{
		new ChatClient();
	}

}
