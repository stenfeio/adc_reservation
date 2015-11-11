import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
            OutgoingThread outgoingThread = null;

            synchronized (this) {
                try {
                    while ((tempRequest = bookingFileReader.readLine()) != null) {
                        requestStringList.add(tempRequest);
                    }
                    System.out.println("Requests loaded into request list...");

                    //closes the bookingFileReader after populating the request array
                    bookingFileReader.close();

                    //converts string requests into Request objects and adds to requestObjectList
                    for (int i = 0; i < requestStringList.size(); i++) {
                        requestObjectList.add(parseRequest(requestStringList.get(i)));
                    }

                    // this part loops over all the requests and starts an outgoing thread for each of them
                    for (int i = 0; i < requestObjectList.size(); i++) {
                        if (currentSystemStatus == Coordinator.Status.NORMAL) {
                            System.out.println("\nProcessing request: " + requestObjectList.get(i));
                            outgoingThread = new OutgoingThread(requestObjectList.get(i));
                            outgoingThread.start();
                            outgoingThread.join();

                        } else if (currentSystemStatus == Coordinator.Status.RECOVERY){
                            System.out.println("Recovering...");
                            currentSystemStatus = Coordinator.Status.NORMAL;
                        } else {
                            this.wait();
                            System.out.println("Cannot request, state not normal");
                            //this.wait();
                            //TODO read objects from file and establish new state.

                        }
                    }

                } catch (IOException e) {
                    System.err.println("Could not read from booking file in coordinator operation thread...");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    System.err.println("Interruption with operation threads...");
                    outgoingThread.interrupt();
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }
        /**causes the operation thread to pause until a notify comes back*/
        public void pauseOperation(){
            synchronized (this){
                try{
                    this.wait();
                }catch (InterruptedException e){

                }
            }
        }

        /**causes the operation thread to resume after a pause*/
        public void resumeOperation(){
            synchronized (this){
                //if(this.isAlive())
                this.notify();
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
                Thread.sleep(3000);
                ObjectOutputStream hotelSocketOut = null;
                ObjectOutputStream concertSocketOut = null;

                ObjectInputStream hotelSocketIn = null;
                ObjectInputStream concertSocketIn = null;

                if(currentSystemStatus == Coordinator.Status.NORMAL) {
                    hotelSocketOut = new ObjectOutputStream(hotelRequestSocket.getOutputStream());
                    concertSocketOut = new ObjectOutputStream(concertRequestSocket.getOutputStream());

                    //send the request to the participant and waits to read a response
                    hotelSocketOut.writeObject(currentRequest);
                    hotelSocketOut.flush();

                    concertSocketOut.writeObject(currentRequest);
                    concertSocketOut.flush();

                    hotelSocketIn = new ObjectInputStream(hotelRequestSocket.getInputStream());
                    hotelResponse = (Request) hotelSocketIn.readObject();

                    concertSocketIn = new ObjectInputStream(concertRequestSocket.getInputStream());
                    concertResponse = (Request) concertSocketIn.readObject();

                    //Democracy! lol
                    if(concertResponse.status == Request.RStatus.SUCCESS &&
                            hotelResponse.status == Request.RStatus.SUCCESS){
                        //TODO write the response Request to the recovery file delay to end
                        currentRequest.status = Request.RStatus.SUCCESS;
                        System.out.println("Booked: " + currentRequest);

                        //confirm result of vote
                        hotelSocketOut.writeObject(currentRequest);
                        concertSocketOut.writeObject(currentRequest);
                        recoveryFileWriter.writeObject(currentRequest);
                    }else{
                        currentRequest.status = Request.RStatus.FAILED;
                        System.out.println("Cancel Booking: " + currentRequest);

                        //confirm result of vote
                        hotelSocketOut.writeObject(currentRequest);
                        concertSocketOut.writeObject(currentRequest);
                        recoveryFileWriter.writeObject(currentRequest);
                    }
                }
                else {
                    System.out.println("Coordinator is in non-normal state! Will not receive requests");
                    //hotelSocketOut.flush();
                    //concertSocketOut.flush();

                    hotelRequestSocket.close();
                    concertRequestSocket.close();
                    return;

                }
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
            catch(InterruptedException e){
                System.err.println("An outgoing thread has been interupted...");
            }
        }
    }

    /**
     * Thread that handles listening to the keyboard input and
     * simulates a fail when "fail" is entered. This will cause
     * a number of changes to the state of the coordinator.
     * The fail recover thread sets the sate of the coordinator to
     * failed and hence causes the operation thread to stop sending messages.
     * This thread also calls the pauseOperation method of the OperationThread
     * class so that it pauses execution.
     */
    protected class FailRecoverThread extends Thread{
        OperationThread operationThread;

        public FailRecoverThread(OperationThread operationThread){
            this.operationThread = operationThread;
        }

        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            if(scanner.nextLine().equals("fail") && currentSystemStatus == Coordinator.Status.NORMAL) {
                currentSystemStatus = Coordinator.Status.FAILED;
                try{
                    System.out.println("Pausing excecution...");
                    //operationThread.pauseOperation();
                    if(scanner.nextLine().equals("recover") && currentSystemStatus == Coordinator.Status.FAILED) {
                        System.out.println("Resuming excecution...");
                        currentSystemStatus = Coordinator.Status.RECOVERY;
                        operationThread.resumeOperation();
                    }
                }catch (Exception e){}
            }else {
                return;
            }
        }
    }
}
