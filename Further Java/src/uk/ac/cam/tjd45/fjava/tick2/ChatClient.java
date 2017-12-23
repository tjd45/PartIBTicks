package uk.ac.cam.tjd45.fjava.tick2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.ac.cam.cl.fjava.messages.ChangeNickMessage;
import uk.ac.cam.cl.fjava.messages.ChatMessage;
import uk.ac.cam.cl.fjava.messages.DynamicObjectInputStream;
import uk.ac.cam.cl.fjava.messages.NewMessageType;
import uk.ac.cam.cl.fjava.messages.RelayMessage;
import uk.ac.cam.cl.fjava.messages.StatusMessage;
import uk.ac.cam.cl.fjava.messages.Execute;


@FurtherJavaPreamble(
		author = "Thomas J. Davidson", 
		crsid = "tjd45", 
		date = "24/10/2016", 
		summary = "Hopefully a working chat client", 
		ticker = FurtherJavaPreamble.Ticker.A)
public class ChatClient {
	

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
			DateFormat initdf = new SimpleDateFormat("HH:mm:ss");
			Date initdateobj = new Date(); 
			
			String inittime = initdf.format(initdateobj) +" ";
			
			System.out.println(inittime + "[Client] Connected to "+server+" on port "+port+".");
			
			Thread output = new Thread() {
				@Override
				public void run() {
					try{
					DynamicObjectInputStream di1 = new DynamicObjectInputStream(s.getInputStream());

					while (1!=2){
						
						
						Object o = di1.readObject();
						
						String message = "";
						if (o instanceof StatusMessage){
							DateFormat df = new SimpleDateFormat("HH:mm:ss");
							Date dateobj = new Date(); 
							
							message = df.format(dateobj) +" ";
							
							message = message + "[Server] "+((StatusMessage) o).getMessage();
						}else{
							if (o instanceof NewMessageType){
									di1.addClass(((NewMessageType) o).getName(), ((NewMessageType) o).getClassData());
									DateFormat df = new SimpleDateFormat("HH:mm:ss");
									Date dateobj = new Date(); 
									
									message = df.format(dateobj) +" ";
									message = message + "[Client] New class " + ((NewMessageType) o).getName() +" loaded.";
								}
							else{
								if (o instanceof RelayMessage){
									DateFormat df = new SimpleDateFormat("HH:mm:ss");
									Date dateobj = new Date(); 
									
									message = df.format(dateobj) +" ";
									message = message + "[" + ((RelayMessage) o).getFrom() +"] "+ ((RelayMessage) o).getMessage();
								}else{
									Class<?> newClass = o.getClass();
									DateFormat df = new SimpleDateFormat("HH:mm:ss");
									Date dateobj = new Date(); 
									
									message = df.format(dateobj) +" ";
					                message = message + "[Client] " + newClass.getSimpleName() + ": ";
					                Field[] fields = newClass.getDeclaredFields();
					                
					                for (int i = 0; i < fields.length; i++) {
					                    fields[i].setAccessible(true);
					                    message = message + fields[i].getName() + "(" + fields[i].get(o) + "), ";


					                }
					                message = message.substring(0, message.length() - 2);
					                System.out.println(message);
					                message = "";
					                Method[] methods = newClass.getDeclaredMethods();
					                
					                for (int i = 0; i < methods.length; i++) {
					                    if (methods[i].getParameterCount() == 0) {
					                        if (methods[i].isAnnotationPresent(Execute.class)) {
					                            methods[i].setAccessible(true);
					                            methods[i].invoke(o);
					                        }
					                    }
					                }

								}
							
							}
						}
						
						
						System.out.println(message);
					}
					}catch (IOException e){
						System.err.println("Error in something");
						return;
					}catch (ClassNotFoundException e){
						System.err.println("Corrupt message sent");
					} catch (IllegalArgumentException e) {
						System.err.println("Corruption in data");
					} catch (IllegalAccessException e) {
						System.err.println("Corruption in data");
					} catch (InvocationTargetException e) {
						System.err.println("Corruption in executing code");
					}
				}
			};
			output.setDaemon(true); 
			output.start();

			ObjectOutputStream do1 = null;

			BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
			do1 = new ObjectOutputStream(s.getOutputStream());
			
			while( 1!=2 ) {
				boolean messagetoprint = false;
				String inputmessage = r.readLine();
				Object o1 = null;
				DateFormat df = new SimpleDateFormat("HH:mm:ss");
				Date dateobj = new Date(); 
				
				String time = df.format(dateobj) +" ";
				if (inputmessage.startsWith("\\")){
					if (inputmessage.startsWith("\\nick")){
						if (inputmessage.length() > 5){
							messagetoprint = true;
							o1 = new ChangeNickMessage(inputmessage.substring(inputmessage.indexOf(' ') + 1,inputmessage.length()));
						}else{
							System.err.println("Enter a nickname");
						}
					}else
						if (inputmessage.startsWith("\\quit")){
							
							System.out.println( time + "[Client] Connection terminated.");
							return;
						}else{
							System.out.println( time+ "[Client] Unknown command \""+ inputmessage.substring(1, inputmessage.indexOf(' ')) +"\"");
						}
						
				}else{
					messagetoprint = true;
					o1 = new ChatMessage(inputmessage);
				}
				
				if (messagetoprint){
					
					
					do1.writeObject(o1);
				
				}
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
