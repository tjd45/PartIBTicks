package uk.ac.cam.tjd45.fjava.tick4;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import uk.ac.cam.cl.fjava.messages.Message;

public class ChatServer {

	public static void main(String args[]){
		boolean validargs = false;
		int port = 0;

		if(args.length==1){
			try{
				port = Integer.parseInt(args[0]);
				validargs = true;
			}
			catch (NumberFormatException e){
				validargs= false;
			}
		}

		final ServerSocket s;
		if (validargs){
			try {
				s = new ServerSocket(port);
				MultiQueue<Message> MessageQ = new MultiQueue<Message>();

				while(1!=2){
					Socket sck = s.accept();
					ClientHandler CH = new ClientHandler(sck,MessageQ);
				}

			} catch (IOException e) {
				System.out.println("Cannot use port number " +port);
				return;
			}
		}else{
			System.out.println("Usage: java ChatServer <port>");
			return;
		}
	}

}
