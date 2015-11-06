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
public class HotelThread {


	 /**
     * Contructor that takes in the lists of Request in order to read the booking requests
     * @param bookingFileReader
     */
	    public HotelThread(){
	      


	    }

	    /**
	     * Private thread that handles opening the incoming socket
	     */
	    protected class IncomingThread extends Thread{
	        //TODO open incoming socket
	        @Override
	        
	        public void run() {
	        	 Socket coordsocket;
	        	try
	            {
	     
	                int port = 8080;
	                ServerSocket serverSocket = new ServerSocket(port);
	                System.out.println("Server Started and listening");
	     
	                //Server is running always. This is done using this while(true) loop
	                while(true)
	                {
	                    //Reading the message from the client
	                   coordsocket = serverSocket.accept();
	                    InputStream is = coordsocket.getInputStream();
	                    InputStreamReader isr = new InputStreamReader(is);
	                    BufferedReader br = new BufferedReader(isr);
	                    String object = br.readLine();
	                    System.out.println("Message received from client is "+object);
	     
	                    //Multiplying the number by 2 and forming the return message
	                    String returnMessage;
	                    try
	                    {
	                       // int numberInIntFormat = Integer.parseInt(object);
	                        String returnValue = "object recieved";
	                        returnMessage = String.valueOf(returnValue) + "\n";
	                    }
	                    catch(NumberFormatException e)
	                    {
	                        //Input was not a number. Sending proper message back to client.
	                        returnMessage = "Please send a proper object\n";
	                    }
	     
	                    //Sending the response back to the client.
	                    OutputStream os = coordsocket.getOutputStream();
	                    OutputStreamWriter osw = new OutputStreamWriter(os);
	                    BufferedWriter bw = new BufferedWriter(osw);
	                    bw.write(returnMessage);
	                    System.out.println("Message sent to the client is "+returnMessage);
	                    bw.flush();
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
	    }

	    protected class OutgoingThread extends Thread{
	        @Override
	        public void run() {

	        }
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
	    protected class OperationThread extends Thread{
	        @Override
	        public void run() {
	            String tempRequest;         //temp string to read requests

	            try {
	                while ((tempRequest = bookingFileReader.readLine()) != null) {
	                    requestArray.add(tempRequest);
	                }
	                System.out.println("Requests loaded into request list...");

	                //closes the bookingFileReader after populating the request array
	                bookingFileReader.close();

	                //TODO parse the requests to make sense of them
	                //converts string requests into Request objects
	                for(int i = 0; i < requestArray.size(); i++) {
	                    requestsList.add(parseRequest(requestArray.get(i)));
	                    System.out.println(requestsList.get(i));
	                }

	            }catch(Exception e){
	                System.err.println("Could not read from booking file in coordinator operation thread...");
	                e.printStackTrace();
	            }
	        }

	        /*
	        Helper method whose job is to convert string requests to Request objects.
	         */
	        private Request parseRequest(String request){
	            String[] tempStrings = request.replace("[", "").replace("]", "").split("\\s");
	            String tempId = tempStrings[0];
	            int tempNumberOfDays = Integer.parseInt(tempStrings[1]);
	            List<Integer> dates = new ArrayList<>();

	            for (int i = 2; i < tempStrings.length; i++){
	                dates.add(Integer.parseInt(tempStrings[i]));
	            }
	            Request tempRequest = new Request(tempId, tempNumberOfDays, dates);

	            return tempRequest;
	        }
	    }

	

}
