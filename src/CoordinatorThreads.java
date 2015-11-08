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
    ObjectOutputStream recoveryFileWriter;
    String hotelAdd, concertAdd;                                //addresses for hotel and concert
    Coordinator.Status currentSystemStatus;

    List<String> requestStringList = new ArrayList<>();         //contains string of requests for participants
    List<Request> requestObjectList = new ArrayList<>();        //contains all Requests for participants

    /**
     * Contructor that takes in the bookingFileReader
     * hotelAdd and concertAdd in order to initialize
     * the booking requests.
     * @param bookingFileReader
     * @param recoveryFileWriter
     * @param hotelAdd
     * @param concertAdd
     * @param currentSystemStatus
     */
    public CoordinatorThreads(BufferedReader bookingFileReader, ObjectOutputStream recoveryFileWriter,
                              String hotelAdd, String concertAdd, Coordinator.Status currentSystemStatus){

        if(bookingFileReader != null)
            this.bookingFileReader = bookingFileReader;
        this.recoveryFileWriter = recoveryFileWriter;

        this.hotelAdd = hotelAdd;
        this.concertAdd = concertAdd;
        this.currentSystemStatus = currentSystemStatus;
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
            }
        }

        /*Helper method whose job is to convert string requests to Request objects */
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
        Request hotelResponse;    //the response request from the participant
        Request concertResponse;

        public OutgoingThread(Request currentRequest){
            this.currentRequest = currentRequest;
        }

        @Override
        public void run() {
            StringTokenizer hStringT = new StringTokenizer(hotelAdd);
            StringTokenizer cStringT = new StringTokenizer(concertAdd);

            System.out.println("Connecting to address: " + hotelAdd);
            System.out.println("Connecting to address: " + concertAdd);

            try(
                    //TODO replace the hard code with the correct addresses
                    Socket hotelRequestSocket = new Socket(hStringT.nextToken(), Integer.parseInt(hStringT.nextToken()));
                    Socket concertRequestSocket = new Socket(cStringT.nextToken(), Integer.parseInt(cStringT.nextToken()))
            ){
                if(currentSystemStatus == Coordinator.Status.NORMAL) {
                    ObjectOutputStream hotelSocketOut = new ObjectOutputStream(hotelRequestSocket.getOutputStream());
                    ObjectOutputStream concertSocketOut = new ObjectOutputStream(concertRequestSocket.getOutputStream());

                    //send the request to the participant and waits to read a response
                    hotelSocketOut.writeObject(currentRequest);
                    hotelSocketOut.flush();

                    concertSocketOut.writeObject(currentRequest);
                    concertSocketOut.flush();

                    ObjectInputStream hotelSocketIn = new ObjectInputStream(hotelRequestSocket.getInputStream());
                    hotelResponse = (Request) hotelSocketIn.readObject();

                    ObjectInputStream concertSocketIn = new ObjectInputStream(concertRequestSocket.getInputStream());
                    concertResponse = (Request) concertSocketIn.readObject();

                    if(concertResponse.status == Request.RStatus.SUCCESS &&
                            hotelResponse.status == Request.RStatus.SUCCESS){
                        //TODO write the response Request to the recovery file delay to end
                        currentRequest.status = Request.RStatus.SUCCESS;
                        System.out.println("Booked: " + currentRequest);

                        //confirm result of vote
                        hotelSocketOut.writeObject(currentRequest);
                        concertSocketOut.writeObject(currentRequest);
                        recoveryFileWriter.writeObject(currentRequest);
                    }
                }

                else
                    System.out.println("Coordinator is in non-normal state! Will not receive requests");

            }
            catch(UnknownHostException e){
                System.err.println("Issue when finding other participants...");
                e.printStackTrace();
            }
            catch (IOException e){
                System.err.println("Issue with communication IO");
                e.printStackTrace();
            }
            catch(ClassNotFoundException e){
                System.err.println("Issue with passing objects to server...");
            }
        }
    }

    protected class FailRecoverThread extends Thread{
        @Override
        public void run() {

        }
    }
}
