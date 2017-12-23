package uk.ac.cam.tjd45.fjava.tick5;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import uk.ac.cam.cl.fjava.messages.Message;

public class ChatServer {

	public static void main(String args[]){
		boolean validargs = false;
		int port = 0;
		String databasepath = "";
		
		if(args.length==2){
			try{
				port = Integer.parseInt(args[0]);
				databasepath = args[1];
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
				Database database = new Database(databasepath);

				while(1!=2){
					Socket sck = s.accept();
					ClientHandler CH = new ClientHandler(sck,MessageQ,database);
				}

			} catch (IOException e) {
				System.out.println("Cannot use port number " +port);
				return;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			System.out.println("Usage: java ChatServer <port> <database path>");
			return;
		}
	}

}
