package uk.ac.cam.tjd45.fjava.tick1;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class StringChat extends Thread{

	public static void main(String[] args) {

		String server = null;
		int port = 0;

		if (args.length != 2){
			System.err.println("This application requires two arguments: <machine> <port>");
			return;
		}else{
			try{ 
				port = Integer.parseInt(args[1]);
				server = args[0];
			}
			catch(NumberFormatException e){
				System.err.println("This application requires two arguments: <machine> <port>");
				return;
			}
		}



		try{
			final Socket s = new Socket(server, port);

			byte[] buffer1 = new byte[1024];
			byte[] buffer2 = new byte[1024];

			Thread output = new Thread() {
				@Override
				public void run() {
					try{
					DataInputStream di1 = new DataInputStream(s.getInputStream());

					while (1!=2){
						int bytesRead = di1.read(buffer1);
						String message = new String(buffer1,0,bytesRead);
						System.out.println(message);
					}
					}catch (IOException e){
						System.err.println("Error in something");
						return;
					}
				}
			};
			output.setDaemon(true); 
			output.start();

			DataOutputStream do1 = null;

			BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
			while( 1!=2 ) {
				String inputmessage = r.readLine();
				buffer2 = inputmessage.getBytes();
				do1 = new DataOutputStream(s.getOutputStream());
				do1.write(buffer2);
				do1.flush();
			}

		} catch (UnknownHostException e) {
			System.err.println("Cannot connect to "+server+" on port "+port);
			return;
		} catch (IOException e) {
			System.err.println("Cannot connect to "+server+" on port "+port);
			return;
		}

		


	}
}