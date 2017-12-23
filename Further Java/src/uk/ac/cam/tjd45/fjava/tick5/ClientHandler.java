package uk.ac.cam.tjd45.fjava.tick5;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.ac.cam.cl.fjava.messages.ChangeNickMessage;
import uk.ac.cam.cl.fjava.messages.ChatMessage;
import uk.ac.cam.cl.fjava.messages.Message;
import uk.ac.cam.cl.fjava.messages.RelayMessage;
import uk.ac.cam.cl.fjava.messages.StatusMessage;

public class ClientHandler {
	private Socket socket;
	private MultiQueue<Message> multiQueue;
	private String nickname;
	private MessageQueue<Message> clientMessages;
	private Database database;//more fields

	public ClientHandler(Socket s, MultiQueue<Message> q, Database d) {
		socket = s;
		multiQueue = q;
		database = d;	

		clientMessages = new SafeMessageQueue();
		List<RelayMessage> recentmessages = new ArrayList<RelayMessage>();

		try {
			recentmessages = database.getRecent();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}



		multiQueue.register(clientMessages);
		Random r = new Random(System.currentTimeMillis());
		nickname = "Anonymous"+(10000 + r.nextInt(20000));

		


		for(RelayMessage message : recentmessages){
			clientMessages.put(message);
		};
		
		StatusMessage sm = new StatusMessage(nickname + " connected from "+socket.getInetAddress().getHostName());
		multiQueue.put(sm);
		
		try {
			database.incrementLogins();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Thread input = new Thread(){
			@Override
			public void run() {
				try{
					ObjectInputStream di1 = new ObjectInputStream(s.getInputStream());

					while (1!=2){


						Object o = di1.readObject();

						if (o instanceof ChangeNickMessage){
							String temp = nickname;
							nickname = ((ChangeNickMessage) o).name;
							StatusMessage message = new StatusMessage(temp +" is now known as " + nickname +".");
							multiQueue.put(message);
						}else if(o instanceof ChatMessage){
							RelayMessage message = new RelayMessage(nickname,(ChatMessage) o);
							multiQueue.put(message);
							database.addMessage(message);
						}
					}
				}
				catch (IOException e){
					StatusMessage message = new StatusMessage(nickname + " has disconnected.");
					multiQueue.deregister(clientMessages);
					multiQueue.put(message);


				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};

		};
		input.setDaemon(true);
		input.start();

		Thread output = new Thread() {
			@Override
			public void run(){
				try{
					ObjectOutputStream do1 = new ObjectOutputStream(s.getOutputStream());

					while (clientMessages!=null){
						do1.writeObject(clientMessages.take());
						do1.flush();
					}
				} catch (IOException e){
					e.printStackTrace();
				}
			}
		};



		output.setDaemon(true);
		output.start();

	};




}

