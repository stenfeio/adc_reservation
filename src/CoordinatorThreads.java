import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class serves as a grouping class for the threads
 * that will run on the coordinator.
 */
public class CoordinatorThreads {

    BufferedReader bookingFileReader;                           //reader for booking requests
    String hotelAdd, concertAdd;                                //addresses for hotel and concert
    List<String> requestStringList = new ArrayList<>();         //contains string of requests for participants
    List<Request> requestObjectList = new ArrayList<>();        //contains all Requests for participants

    /**
     * Contructor that takes in the bookingFileReader
     * hotelAdd and concertAdd in order to initialize
     * the booking requests.
     * @param bookingFileReader
     * @param hotelAdd
     * @param concertAdd
     */
    public CoordinatorThreads(BufferedReader bookingFileReader, String hotelAdd, String concertAdd){
        if(bookingFileReader != null)
            this.bookingFileReader = bookingFileReader;

        this.hotelAdd = hotelAdd;
        this.concertAdd = concertAdd;
    }

    /**
     * This class is a thread that handles reading the requests
     * from the bookingRequests file and populates the request list.
     * It should also handle forwarding the requests to the outgoing
     * thread.
     */
    protected class OperationThread extends Thread{
        @Override
        public void run() {
            String tempRequest;         //temp string to read requests

            try {
                while ((tempRequest = bookingFileReader.readLine()) != null) {
                    requestStringList.add(tempRequest);
                }
                System.out.println("Requests loaded into request list...");

                //closes the bookingFileReader after populating the request array
                bookingFileReader.close();

                //converts string requests into Request objects and adds to requestObjectList
                for(int i = 0; i < requestStringList.size(); i++) {
                    requestObjectList.add(parseRequest(requestStringList.get(i)));
                }

                // this part loops over all the requests and starts an outgoing thread for each of them
                for(int i = 0; i < requestObjectList.size(); i++) {
                    System.out.println("Processing request: " + requestObjectList.get(i));
                    OutgoingThread outgoingThread = new OutgoingThread(requestObjectList.get(i));
                    outgoingThread.start();
                    outgoingThread.join();
                }

            }catch(IOException e){
                System.err.println("Could not read from booking file in coordinator operation thread...");
                e.printStackTrace();
            }catch (InterruptedException e){
                System.err.println("Interruption with outgoing threads...");
                e.printStackTrace();
                return;
            }
        }

        /*
         *Helper method whose job is to convert string requests to Request objects.
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

    /**
     * Thread that handles opening the incoming socket
     */
    protected class IncomingThread extends Thread{
        //TODO open incoming socket
        @Override
        public void run() {

        }
    }

    /**
     * Thread that handles passing the request to the
     * hotel and concert participants and waits for feedback
     * from them.
     */
    protected class OutgoingThread extends Thread{
        Request currentRequest;     //current request to handle
        Request responseRequest;    //the response request from the participant

        public OutgoingThread(Request currentRequest){
            this.currentRequest = currentRequest;
        }

        @Override
        public void run() {
            StringTokenizer st = new StringTokenizer(hotelAdd);
            System.out.println("Connecting to " + hotelAdd);

            try(     //TODO replace the hard code with the correct addresses
                    Socket requestSocket = new Socket("localhost", 7);//st.nextToken(), Integer.parseInt(st.nextToken()));
                    PrintWriter out = new PrintWriter(requestSocket.getOutputStream(),true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
                    //ObjectOutputStream outputStream = new ObjectOutputStream(requestSocket.getOutputStream());
                    //ObjectInputStream inputStream = new ObjectInputStream(requestSocket.getInputStream())
            ){
                out.println(currentRequest.toString());
                System.out.println("echo: " + in.readLine() +"\n");

                //send the request to the participant and waits to read a response
                //outputStream.writeObject(currentRequest);
                //responseRequest = (Request) inputStream.readObject();

                requestSocket.close();
                out.close();
                in.close();
                //outputStream.close();
                //inputStream.close();

            }
            catch(UnknownHostException e){
                System.err.println("Issue when finding other participants...");
                e.printStackTrace();
            }
            catch (IOException e){
                System.err.println("Issue with communication IO");
                e.printStackTrace();
            }
//            catch(ClassNotFoundException e){
//
//            }

        }
    }

    protected class FailRecoverThread extends Thread{
        @Override
        public void run() {

        }
    }
}
