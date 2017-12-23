package uk.ac.cam.tjd45.fjava.tick5;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.cl.fjava.messages.ChatMessage;
import uk.ac.cam.cl.fjava.messages.RelayMessage;

public class DatabaseTester {

	public static void main(String args[]){

		Database database = null;
		try{
			database = new Database("tjd45/Test");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();}

		ChatMessage cm = new ChatMessage("hello");
		RelayMessage message = new RelayMessage("Tom",cm);

		try {


			database.incrementLogins();

			database.addMessage(message);
			List<RelayMessage> recentmessages = new ArrayList<RelayMessage>();
			recentmessages.add(message);

			try {
				recentmessages = database.getRecent();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


			for(RelayMessage rm : recentmessages){

				System.out.println(rm.getFrom());
			} 


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
