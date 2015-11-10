	import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.*;


	/**
	 * This class serves as a grouping class for the threads that will run on the Concert.

	    List<String> requestArray = new ArrayList<>();      //contains string of requests for participants
	    List<Request> requestsList = new ArrayList<>();     //contains all Requests for participants

	   


	 /**
     * Contructor that takes in the lists of Request in order to read the booking requests
     * @param bookingFileReader
     */
	    public class ConcertThread extends Thread{
	      
	    	final String resourcePath = System.getProperty("user.dir") + "\\resources\\";
	    	 File concertlogfile;						//Defines the log file for concert
	    	 BufferedWriter concertlogfilewrite;		//Defines the log file writer
	    	 
	         Concert concert;
	      
	   
	        Socket coordsocket;
	        ObjectInputStream inStream=null;
	        ObjectOutputStream outstream=null;
	        ObjectInputStream inStream1=null;
	       // Thread conthread;
	        
	       ConcertThread(Concert c){
	        	concert=c;
	        	concertlogfile = new File(resourcePath+ "Concert -Log.txt");
	        }
	        public void run() {
	        	 /**
	    	     *  thread that handles opening the incoming socket
	    	     */
	    	   
	    	        //TODO open incoming socket
	    	    //    @Override
	        	try
	            {
	     
	                int port = 8;
	                ServerSocket serverSocketcon = new ServerSocket(port);
	                System.out.println("Server Started and listening");
	               concert.SYSTEM_STATUS= Coordinator.Status.NORMAL;
	     
	                //Server is running always. This is done using this while(true) loop
	                while(true){
	                    //Reading the message from the client
	                     coordsocket = serverSocketcon.accept();
	                   //  System.out.println("Object accepted");
	                     inStream = new ObjectInputStream(coordsocket.getInputStream());
	                     Request request = (Request)inStream.readObject();
                         System.out.println("Object recieved "+request);
                         
                         
                         
                         //Check the system status
                         if( concert.SYSTEM_STATUS== Coordinator.Status.NORMAL){
                        	 Request request1= checkstatus(request);		//Call the method to check availability of tickets
                   	     System.out.println(request1);
                   	  
                   	     //Writing the object to the client
                   	     outstream = new ObjectOutputStream(coordsocket.getOutputStream()); 
               	         outstream.writeObject(request1);
               	      
               	         System.out.println("Message sent to the client is "+request1);
               	         //outstream.flush();
               	         
               	         //Read the object again to check if the reservation can be done or not
               	        // conthread.wait(10);
               	         
               	    
               	    
               	      
               	         Request req=(Request)inStream.readObject();
               	         if(req.status==Request.RStatus.SUCCESS){
               	        	 System.out.println("go in reserv");
               	         Reservation(req);
               	         }
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
		 //	System.out.println("");
		   for(int i=0;i<request.dates.size();i++){
			   n[i]=request.dates.get(i);
			   System.out.println("Concert ticket available on day "+n[i]+concert.Tickets[n[i]]);
		   if(concert.Tickets[n[i]-1]<request.numberOfDays){
			 	temp++;
			   break;}
		   
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
			   concert.Tickets[n[i]-1]=concert.Tickets[n[i]-1]-request.numberOfDays;
				  
			   }
		 String A="";
		 for(int i=0;i<10;i++){
			 int j=i+1;
			 A=A.concat(Integer.toString(j));
			 A= A.concat(" ");
			 System.out.println("Tickets on day "+ j+"="+concert.Tickets[i]);
			 A=A.concat(Integer.toString(concert.Tickets[i]));
			 A=A.concat("\n");
			
			 openFiles();
			 
			 concertlogfilewrite.write(A);
			 concertlogfilewrite.write("\n");
			 
		 }
		 concertlogfilewrite.flush();
		 concertlogfilewrite.close(); 
	 }
	 
	 private void openFiles(){
	        try{
	        	
	        	concertlogfilewrite = new BufferedWriter(new FileWriter(concertlogfile));

	        }catch(FileNotFoundException e){
	            System.err.println("Could not find configuration file...");
	            e.printStackTrace();
	        }catch (IOException e){
	            System.err.println("Can't write to concert-Log file...");
	            e.printStackTrace();
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

