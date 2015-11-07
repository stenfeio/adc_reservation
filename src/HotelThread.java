	import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.*;


	/**
	 * This class serves as a grouping class for the threads that will run on the Hotel.

	    List<String> requestArray = new ArrayList<>();      //contains string of requests for participants
	    List<Request> requestsList = new ArrayList<>();     //contains all Requests for participants

	    /**
	     * Contructor that takes in the bookingFileReader in order to read the booking requests
	     * @param bookingFileReader
	     */


	 /**
     * Contructor that takes in the lists of Request in order to read the booking requests
     * @param bookingFileReader
     */
	    public class HotelThread extends Thread{
	      

	    Hotel hotel;
	    /**
	     * Private thread that handles opening the incoming socket
	     */
	   
	        //TODO open incoming socket
	    //    @Override
	        Socket coordsocket;
	        ObjectInputStream inStream=null;
	        ObjectOutputStream outstream=null;
	        //Hotel h=new Hotel();
	        HotelThread(Hotel h){
	        	hotel=h;
	        }
	        public void run() {
	        	 
	        	try
	            {
	     
	                int port = 7;
	                ServerSocket serverSocket = new ServerSocket(port);
	                System.out.println("Server Started and listening");
	     
	                //Server is running always. This is done using this while(true) loop
	                while(true)
	                {
	                    //Reading the message from the client
	                   coordsocket = serverSocket.accept();
	                   inStream = new ObjectInputStream(coordsocket.getInputStream());
                        Request request = (Request) inStream.readObject();
	                  //  InputStream is = coordsocket.getInputStream();
	                  //  InputStreamReader isr = new InputStreamReader(is);
	                   // BufferedReader br = new BufferedReader(isr);
	                  //  String object = br.readLine();
	                    System.out.println("Object recieved "+request);
						outstream = new ObjectOutputStream(coordsocket.getOutputStream());
						outstream.writeObject(request);
	     
	                    //Multiplying the number by 2 and forming the return message
	                    String returnMessage;
	                    try
	                    {	//Check if the object is passed with the correct parameter or not
	                    	if(request.id!=null ||request.dates!=null|| request.numberOfDays!=0 ||request.status!=null){
	                       
	                    	 Request request1= checkstatus(request);
	                    	 String returnValue = "Success";
		                        returnMessage = String.valueOf(returnValue) + "\n";
	                    	 outstream = new ObjectOutputStream(coordsocket.getOutputStream());
	                    	 outstream.writeObject(request1);
	                    }	
	                    else{
	                    	String returnValue = "Recovery";
	                        returnMessage = String.valueOf(returnValue) + "\n";
	                        outstream = new ObjectOutputStream(coordsocket.getOutputStream());
	                        outstream.writeObject(request);
	                    }
	                    }
	                    catch(Exception e)
	                    {
	                       
	                        returnMessage = "Please send a proper object\n";
	                    }
	                   // Request request1= checkstatus(request);  
	                    //Sending the response back to the client.
	                    
	                    //outstream = new ObjectOutputStream(coordsocket.getOutputStream());
	                  //  Request request= new Request();
	                 //   outstream.writeObject(request1);
	                   // OutputStreamWriter osw = new OutputStreamWriter(os);
	                  //  BufferedWriter bw = new BufferedWriter(osw);
	                  //  bw.write(returnMessage);
	                    System.out.println("Message sent to the client is "+returnMessage);
	                  //  bw.flush();
	                }
	            }
	            catch (Exception e)
	            {
	                e.printStackTrace();
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
	    
	   Request checkstatus(Request request){
		   for(int i=0;i<9;i++){
			 
		   if(hotel.Rooms[request.dates.get(i)]!=0)
			   request.status=Request.RStatus.SUCCESS;
		   else
			   request.status=Request.RStatus.FAILED;
		   }
		   return request;
	   }
	   

	    protected class FailRecoverThread extends Thread{
	        @Override
	        public void run() {

	        }
	    }

	    /**
	     * This class is a thread that handles reading the requests
	     * from the bookingRequests file and populates the request list.
	     * It should also handle forwarding the resquests to the outgoing
	     * thread.
	     */
	   

	

}
