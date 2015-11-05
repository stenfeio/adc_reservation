import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class serves as a grouping class for the threads that will run on the coordinator.
 */
public class CoordinatorThreads {

    BufferedReader bookingFileReader;                   //reader for booking requests
    List<String> requestArray = new ArrayList<>();      //contains string of requests for participants
    List<Request> requestsList = new ArrayList<>();     //contains all Requests for participants

    /**
     * Contructor that takes in the bookingFileReader in order to read the booking requests
     * @param bookingFileReader
     */
    public CoordinatorThreads(BufferedReader bookingFileReader){
        if(bookingFileReader != null)
            this.bookingFileReader = bookingFileReader;


    }

    /**
     * Private thread that handles opening the incoming socket
     */
    protected class IncomingThread extends Thread{
        //TODO open incoming socket
        @Override
        public void run() {

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
