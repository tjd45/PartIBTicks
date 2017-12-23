package uk.ac.cam.tjd45.fjava.tick1;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class StringReceive {

	public static void main(String[] args) {

		if (args.length != 2){
			System.err.println("This application requires two arguments: <machine> <port>");
			return;
		}
		else{
			try{ 
				int PortNumber = Integer.parseInt(args[1]);
				Socket MySocket;
				DataInputStream di1;
				byte[] buffer = new byte[1024];

				String MachineName = args[0];

				try {
					MySocket = new Socket(MachineName, PortNumber);
					di1 = new DataInputStream(MySocket.getInputStream());

					while (1!=2){
						int bytesRead = di1.read(buffer);
						String message = new String(buffer,0,bytesRead);
						System.out.println(message);

					}
				} catch (UnknownHostException e) {
					System.err.println("Cannot connect to "+MachineName+" on port "+PortNumber);
					return;
				} catch (IOException e) {
					System.err.println("Cannot connect to "+MachineName+" on port "+PortNumber);
					return;
				}



			}catch(NumberFormatException e){
				System.err.println("This application requires two arguments: <machine> <port>");
				return;
			}

		}


	}

}
