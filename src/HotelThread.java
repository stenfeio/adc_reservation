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
	       
	        HotelThread(Hotel h){
	        	hotel=h;
	        }
	        public void run() {
	        	 
	        	try
	            {
	     
	                int port = 7;
	                ServerSocket serverSocket = new ServerSocket(port);
	                System.out.println("Server Started and listening");
	               hotel.SYSTEM_STATUS= Coordinator.Status.NORMAL;
	     
	                //Server is running always. This is done using this while(true) loop
	                while(true){
	                    //Reading the message from the client
	                     coordsocket = serverSocket.accept();
	                     System.out.println("Object accepted");
	                     inStream = new ObjectInputStream(coordsocket.getInputStream());
	                     Request request = (Request) inStream.readObject();
                         System.out.println("Object recieved "+request);
                         
                         //Check the system status
                         if( hotel.SYSTEM_STATUS== Coordinator.Status.NORMAL){
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
	                  
	                  /*  try
	                    {	//Check if the object is passed with the correct parameter or not
	                    	System.out.println("checks the paraemeter of the object");
	                    	if(request.id!=null ||request.dates!=null|| request.numberOfDays!=0 ||request.status!=null){
	                    		System.out.println("correct");
	                    	 outstream = new ObjectOutputStream(coordsocket.getOutputStream());
	                    	 outstream.writeObject(request1);
	                    	 System.out.println("Message sent to the client is "+request1);
	                    }	
	                    else{
	                        outstream = new ObjectOutputStream(coordsocket.getOutputStream());
	                        outstream.writeObject(request);
	                       
	                    }
	                    }
	                    catch(Exception e)
	                    {
	                       
	                    }
	                  
				                   OutputStreamWriter osw = new OutputStreamWriter(os);
				                   BufferedWriter bw = new BufferedWriter(osw);
				                   bw.write(returnMessage);
				                   bw.flush();
	                 */
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
	    
	        /* Method to perform operation on the object*/
	 public  Request checkstatus(Request request){
		 	int[] n=new int[request.dates.size()];
		 	int temp=0;
		 	
		   for(int i=0;i<request.dates.size();i++){
			   n[i]=request.dates.get(i);
		   if(hotel.Rooms[n[i]]<request.numberOfDays)
			   temp++;
		   }
		   if(temp==0)
			   request.status=Request.RStatus.SUCCESS;
		   else
			   request.status=Request.RStatus.FAILED;
		   return request;
	   }
	  
	 public void Reservation(Request request){
		 int[] n=new int[request.dates.size()];
		 
		 for(int i=0;i<request.dates.size();i++){
			 n[i]=request.dates.get(i);
			   hotel.Rooms[n[i]]=hotel.Rooms[n[i]]-request.numberOfDays;
				  
			   }
	 }
	        
	 /*   protected class FailRecoverThread extends Thread{
	        @Override
	        public void run() {

	        }
	    }*/

	    /**
	     * This class is a thread that handles reading the requests
	     * from the bookingRequests file and populates the request list.
	     * It should also handle forwarding the resquests to the outgoing
	     * thread.
	     */
	   

	

}

