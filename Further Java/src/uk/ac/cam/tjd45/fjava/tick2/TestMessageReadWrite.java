package uk.ac.cam.tjd45.fjava.tick2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;

//TODO: import required classes

class TestMessageReadWrite {

	static boolean writeMessage(String message, String filename) {

		TestMessage newmessage = new TestMessage();
		newmessage.setMessage(message);
		
		FileOutputStream fos;

		newmessage.setMessage(message);

		ObjectOutputStream out;
		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(newmessage);
			out.close();
		} catch (IOException e) {
			return false;
		}
		return true;


	}




	static String readMessage(String location) {

		TestMessage input = null;
		ObjectInputStream in;

		try {
			if (location.startsWith("http://")) {

				URL address = new URL(location);
				URLConnection connection = address.openConnection();
				InputStream di = connection.getInputStream();
				in = new ObjectInputStream(di);
				
				input = (TestMessage) in.readObject();
				return input.getMessage();

			} else {

				FileInputStream filein = new FileInputStream(location);
				in = new ObjectInputStream(filein);

				input = (TestMessage) in.readObject();
				return input.getMessage();
			}
		} catch (ClassNotFoundException | IOException e) {
			return null;
		}
	
	
	}

	


	public static void main(String args[]) {
		System.out.println(readMessage("helloworld.txt"));
	}
}