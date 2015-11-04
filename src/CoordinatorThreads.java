import java.io.BufferedReader;

/**
 * This class serves as a grouping class for the threads that will run on the coordinator.
 */
public class CoordinatorThreads {
    BufferedReader bookingFileReader;

    public CoordinatorThreads(BufferedReader bookingFileReader){
        this.bookingFileReader = bookingFileReader;
    }
    private class IncomingThread extends Thread{
        //TODO open incoming socket
        @Override
        public void run() {

        }
    }

    private class OutgoingThread extends Thread{
        @Override
        public void run() {

        }
    }

    private class FailRecoverThread extends Thread{
        @Override
        public void run() {

        }
    }

    private class OperationThread extends Thread{
        //TODO start reading from file
        @Override
        public void run() {

        }
    }

}
