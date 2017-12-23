package uk.ac.cam.tjd45.fjava.tick5;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import uk.ac.cam.cl.fjava.messages.ChatMessage;
import uk.ac.cam.cl.fjava.messages.RelayMessage;

public class Database {

	private Connection connection;

	public Database(String databasePath) throws SQLException {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connection = DriverManager.getConnection("jdbc:hsqldb:file:"+databasePath,"SA","");

		Statement delayStmt = connection.createStatement();
		try {delayStmt.execute("SET WRITE_DELAY FALSE");}  //Always update data on disk
		finally {delayStmt.close();}

		connection.setAutoCommit(false);

		Statement sqlStmt = connection.createStatement();
		try {
			sqlStmt.execute("CREATE TABLE messages(nick VARCHAR(255) NOT NULL,"+
					"message VARCHAR(4096) NOT NULL,timeposted BIGINT NOT NULL)");
		} catch (SQLException e) {
			System.out.println("Warning: Database table \"messages\" already exists.");
		} finally {
			sqlStmt.close();
		}

		Statement sqlStmt2 = connection.createStatement();
		try {
			sqlStmt2.execute("CREATE TABLE statistics(key VARCHAR(255),value INT)");
			sqlStmt2.execute("INSERT INTO statistics(key,value) VALUES ('Total messages',0)");
			sqlStmt2.execute("INSERT INTO statistics(key,value) VALUES ('Total logins',0)");
		} catch (SQLException e) {
			System.out.println("Warning: Database table \"statistics\" already exists.");
		} finally {
			sqlStmt2.close();
		}

		connection.commit();

	}

	public void close() throws SQLException { connection.close(); }

	public void incrementLogins() throws SQLException { 
		Statement sqlLoginStmt = connection.createStatement();
		try{
			sqlLoginStmt.execute("UPDATE statistics SET value = value+1 WHERE key='Total logins'");
		} finally{
			sqlLoginStmt.close();
		}

		connection.commit();
		
	}

	public void addMessage(RelayMessage m) throws SQLException {
		String stmt = "INSERT INTO MESSAGES(nick,message,timeposted) VALUES (?,?,?)";
		PreparedStatement insertMessage = connection.prepareStatement(stmt);
		try {
			insertMessage.setString(1, m.getFrom()); 
			insertMessage.setString(2, m.getMessage());
			insertMessage.setLong(3, m.getCreationTime().getTime());
			insertMessage.executeUpdate();
		} finally { 
			insertMessage.close();
		}
		
		Statement sqlLoginStmt = connection.createStatement();
		try{
			sqlLoginStmt.execute("UPDATE statistics SET value = value+1 WHERE key='Total messages'");
		} finally{
			sqlLoginStmt.close();
		}
		
		connection.commit();
	}

	public List<RelayMessage> getRecent() throws SQLException { 
		
		List<RelayMessage> messages = new ArrayList<RelayMessage>();
		
		String stmt = "SELECT nick,message,timeposted FROM messages ORDER BY timeposted DESC LIMIT 10";
		PreparedStatement recentMessages = connection.prepareStatement(stmt);
		try {
			ResultSet rs = recentMessages.executeQuery();
			try {
				ArrayList<RelayMessage> tempresults = new ArrayList<RelayMessage>();
				
				while (rs.next()){
					ChatMessage cm = new ChatMessage(rs.getString(2));
					RelayMessage rm = new RelayMessage(rs.getString(1),cm);
					tempresults.add(rm);
				}
				
				ListIterator<RelayMessage> li = tempresults.listIterator(tempresults.size());

				// Iterate in reverse.
				while(li.hasPrevious()) {
				  messages.add(li.previous());
				}
				
					
			} finally {
				rs.close();
			}

		} finally {
			recentMessages.close();
		}

		
		return messages;
		
	}


	public static void main(String[] args){
		String database = "";

		if(args.length == 1){
			database = args[0];
		}else{
			System.err.println("Usage: java uk.ac.cam.tjd45.fjava.tick5.Database <database name>");
			return;
		}

		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try{
			Connection connection = DriverManager.getConnection("jdbc:hsqldb:file:"+database,"SA","");

			Statement delayStmt = connection.createStatement();
			try {delayStmt.execute("SET WRITE_DELAY FALSE");}  //Always update data on disk
			finally {delayStmt.close();}

			connection.setAutoCommit(false);

			Statement sqlStmt = connection.createStatement();
			try {
				sqlStmt.execute("CREATE TABLE messages(nick VARCHAR(255) NOT NULL,"+
						"message VARCHAR(4096) NOT NULL,timeposted BIGINT NOT NULL)");
			} catch (SQLException e) {
				System.out.println("Warning: Database table \"messages\" already exists.");
			} finally {
				sqlStmt.close();
			}


			String stmt = "INSERT INTO MESSAGES(nick,message,timeposted) VALUES (?,?,?)";
			PreparedStatement insertMessage = connection.prepareStatement(stmt);
			try {
				insertMessage.setString(1, "Alastair"); //set value of first "?" to "Alastair"
				insertMessage.setString(2, "Hello, Andy");
				insertMessage.setLong(3, System.currentTimeMillis());
				insertMessage.executeUpdate();
			} finally { //Notice use of finally clause here to finish statement
				insertMessage.close();
			}


			connection.commit();

			stmt = "SELECT nick,message,timeposted FROM messages "+
					"ORDER BY timeposted DESC LIMIT 10";
			PreparedStatement recentMessages = connection.prepareStatement(stmt);
			try {
				ResultSet rs = recentMessages.executeQuery();
				try {
					while (rs.next())
						System.out.println(rs.getString(1)+": "+rs.getString(2)+
								" ["+rs.getLong(3)+"]");
				} finally {
					rs.close();
				}

			} finally {
				recentMessages.close();
			}


			connection.close();


		} catch (SQLException e){
			e.printStackTrace();
		}

	}
}
