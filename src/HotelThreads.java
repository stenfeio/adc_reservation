import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class HotelThreads {
	Hotel hotel;
	HotelThread ht=new HotelThread();
	FailRecoverThread ft=new FailRecoverThread();
	
	HotelThreads(Hotel h){
		hotel=h;
		ft.start();
		try{
			ht.start();
			ht.join();
			System.out.println("Back to main execution...");
			HotelThread ht2 = new HotelThread();
			ht2.start();
			System.out.println("Started new thread");
			//ht2.join();

        }catch (Exception e){
            System.err.println("Interruption with operation thread...");
            //e.printStackTrace();
			HotelThread ht2 = new HotelThread();
			ht2.start();
        }
	}

	public class HotelThread extends Thread{

		 final String resourcePath = System.getProperty("user.dir") + "\\resources\\";
		 File hotellogfile;						//Defines the log file for hotel
		 BufferedWriter hotellogfilewrite;		//Defines the log file writer
		 BufferedReader hotelbufferedreader;	//Defines the log file reader

		Socket coordsocket;						//Defines the object for socket class
		ObjectInputStream inStream=null;		//Defines the object for ObjectInputStream
		ObjectOutputStream outstream=null;		//Defines the object for ObjectOutputStream

		/*
		 * Constructor to initialize the Hotel object and file path
		 */
		HotelThread(){
			hotellogfile = new File(resourcePath+ "Hotel-Log.txt");
		}
		public void run() {
			 /**
			 *  thread that handles opening the incoming socket
			 */

			//TODO open incoming socket
			//    @Override

			try
			{
				int port = 7;
				System.out.println("Inside hotel thread...");
				ServerSocket serverSocket = new ServerSocket(port);
				System.out.println("Server Started and listening");
			    hotel.SYSTEM_STATUS= Coordinator.Status.NORMAL;

				//Server is running always. This is done using this while(true) loop
				while(true){
					//Reading the message from the client
					 coordsocket = serverSocket.accept();
					 System.out.println("Object accepted");
					 Thread.sleep(3000);
					 if( hotel.SYSTEM_STATUS== Coordinator.Status.NORMAL){
						 inStream = new ObjectInputStream(coordsocket.getInputStream());
						 Request request = (Request) inStream.readObject();
						 System.out.println("Object recieved "+request);

						 //Check the system status

						 Request request1= checkstatus(request);		//Call the method to check availability of rooms
						 System.out.println(request1);

						 //Writing the object to the client
						 outstream = new ObjectOutputStream(coordsocket.getOutputStream());
						 outstream.writeObject(request1);
						 System.out.println("Message sent to the client is "+request1);

						 //Read the object again to check if the reservation can be done or not
						 Request req=(Request)inStream.readObject();
						 if(req.status==Request.RStatus.SUCCESS)
							Reservation(req);
					 }
					 else if(hotel.SYSTEM_STATUS==Coordinator.Status.RECOVERY){
						 System.out.println("Hotel system is in Recovery State");
						 ReadFile();
						 hotel.SYSTEM_STATUS=Coordinator.Status.NORMAL;
					 }
					 else{
						 for(int i=0;i<10;i++)
						 hotel.Rooms[i]=8;				            			//Set to Default Value
						 inStream=null;
						 outstream=null;
						 System.out.println("Hotel system is in Fail State, It won't recieve");
						 //Thread.sleep(300000000);
					 }
				}
		   	}
			catch (ClassNotFoundException e){

			}
			catch(InterruptedException e){

			}
			catch(IOException e){
				System.out.println("Client dropped.");
				return;
			}
			finally
			{
				try
				{
					coordsocket.close();
				}
				catch(Exception e){}
			}
		}

		/* Method to perform operation on the object*/
		 public  Request checkstatus(Request request){
				int[] n=new int[request.dates.size()];
				int temp=0;

			   for(int i=0;i<request.dates.size();i++){
				   n[i]=request.dates.get(i);
			   if(hotel.Rooms[n[i]-1]<request.numberOfDays)
				   temp++;
			   }
			   if(temp==0)
				   request.status=Request.RStatus.SUCCESS;
			   else
				   request.status=Request.RStatus.FAILED;
			   return request;
		 }

		 public void Reservation(Request request) throws IOException{
			 int[] n=new int[request.dates.size()];

			 for(int i=0;i<request.dates.size();i++){
				 n[i]=request.dates.get(i);
				   hotel.Rooms[n[i]-1]=hotel.Rooms[n[i]-1]-request.numberOfDays;

				   }
			String A="";
			 for(int i=0;i<10;i++){
				 int j=i+1;
				 A=A.concat(Integer.toString(j));
				 A= A.concat(" ");
				 System.out.println("Rooms on day "+ j+"="+hotel.Rooms[i]);
				 A=A.concat(Integer.toString(hotel.Rooms[i]));
				 A=A.concat("\n");

				 openFiles();

				 hotellogfilewrite.write(A);
				 hotellogfilewrite.write("\n");

			 }
			 hotellogfilewrite.flush();
			 hotellogfilewrite.close();
		 }

   void openFiles(){
		try{
			hotellogfilewrite = new BufferedWriter(new FileWriter(hotellogfile));

		}catch(FileNotFoundException e){
			System.err.println("Could not find configuration file...");
			e.printStackTrace();
		}catch (IOException e){
			System.err.println("Can't write to Hotel-Log file...");
			e.printStackTrace();
		}
	}

   public void ReadFile(){
	   String line;
	   openFiles();
		 if (hotelbufferedreader!= null) {
			 try {
				 line = hotelbufferedreader.readLine();							//reads the address of the coordinator given in file

				 for(int i=0;i<10;i++){
					String[] temp;													//String split to read the no. of rooms from file
				 line = hotelbufferedreader.readLine();
				 String delimiter = " ";
				 temp=line.split(delimiter);
				 hotel.Rooms[i]=Integer.valueOf(temp[1]);		                              //Initialize the rooms with the value given in file

				 // concertAdd = line;
				 System.out.println("Rooms on the day"+i+"  " +hotel.Rooms[i]);
				 }

				 line = hotelbufferedreader.readLine();

				 hotelbufferedreader.close();
				 System.out.println("Finished setting ip addresses...\nConfigFileReader closing");

			 }catch (Exception e){
				 System.err.println("Issue with setting addresses...");
				 e.printStackTrace();
			 }
		 }
   }

 }
	    
	    
	protected class FailRecoverThread extends Thread{
		@Override
		public void run() {
			 Scanner scanner = new Scanner(System.in);
			 if(scanner.next().equals("fail") && hotel.SYSTEM_STATUS == Coordinator.Status.NORMAL) {
				 System.out.println("Inside fail thread.");
				 hotel.SYSTEM_STATUS = Coordinator.Status.FAILED;
				 try{
					 Thread.sleep(2000);
				 }catch (InterruptedException e){

				 }
			 }
			 if(scanner.next().equals("rec") && hotel.SYSTEM_STATUS==Coordinator.Status.FAILED){
				 System.out.println("Changed to recovery state");
				 hotel.SYSTEM_STATUS = Coordinator.Status.RECOVERY;
				 try{
					 Thread.sleep(200);
				 }catch (InterruptedException e){

				 }
			//	 ht.interrupt();

			 }
		}
	}
}
