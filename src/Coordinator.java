import java.io.*;

public class Coordinator {

    //enum structure that represents component state
    public enum Status{
        NORMAL, RECOVERY, FAILED
    }

    //path to resources
    final String resourcePath = "C:\\Users\\Talal\\IdeaProjects\\adc_reservation\\resources\\";

    Status SYSTEM_STATUS;               //System status variable

    File configFile;                    //Defines config file path
    BufferedReader configFileReader;    //Defines config file reader

    File bookingFile;                   //Defines booking file path
    BufferedReader bookingFileReader;   //Defines booking file reader

    File recoveryFile;                  //Defines recovery file path
    BufferedWriter recoveryFileWriter;  //Defines recover file writer

    String hotelAdd;                    //Address for hotel participant
    String concertAdd;                  //Address for concert participant.

    CoordinatorThreads threadManager;

    /**
     * Constructor to setup a coordinator object. Sets up the config file and sets the initial
     * state to failed. Failed changes when we establish a running thread with a port.
     */
    public Coordinator(){
        SYSTEM_STATUS = Status.FAILED;
        configFile = new File(resourcePath+ "coordinator-config.txt");
        recoveryFile = new File(resourcePath+ "coordinator-recovery.txt");

        openFiles();
        setIAddresses();

        //Initializes the thread manager with a handle to the booking file
        threadManager = new CoordinatorThreads(bookingFileReader, hotelAdd, concertAdd);
        initializeThreads();

    }

    /*
     * Helper method to open files. Throws exceptions if files not found.
     * @param configFile
     * @param recoveryFile
     */
    private void openFiles(){
        try{
            configFileReader = new BufferedReader(new FileReader(configFile));
            recoveryFileWriter = new BufferedWriter(new FileWriter(recoveryFile));

        }catch(FileNotFoundException e){
            System.err.println("Could not find config file...");
            e.printStackTrace();
        }catch (IOException e){
            System.err.println("Can't write to recovery file...");
            e.printStackTrace();
        }
    }

    /*
     * Helper method to close files. Throws IOException if somethings wrong.
     */
    private void closeFiles(){
        try{
            if (configFileReader != null)
                configFileReader.close();
            if(bookingFileReader != null)
                bookingFileReader.close();
            if (recoveryFileWriter !=null)
                recoveryFileWriter.close();

        }catch(IOException e){
            System.err.println("Issue with closing files!");
            e.printStackTrace();
        }
    }

    /*
     * Helper method to set the values of the other participants and opens the reservation file.
     */
    private void setIAddresses(){
        String line;
        System.out.println("Reading from config file...");
        if (configFileReader != null) {
            try {
                line = configFileReader.readLine();
                hotelAdd = line;

                line = configFileReader.readLine();
                concertAdd = line;

                line = configFileReader.readLine();
                bookingFile = new File(resourcePath+line);
                bookingFileReader = new BufferedReader(new FileReader(bookingFile));

                configFileReader.close();
                System.out.println("Finished setting ip addresses...\nConfigFileReader closing");

            }catch (Exception e){
                System.err.println("Issue with setting addresses...");
                e.printStackTrace();
            }
        }
    }

    /*
    The goal of this method is to initialize the threads that run on the coordinator
     */
    private void initializeThreads(){
        CoordinatorThreads.OperationThread ot = threadManager.new OperationThread();

        this.SYSTEM_STATUS = Status.NORMAL;
        try{
            ot.join();
            ot.run();
        }catch (InterruptedException e){
            System.out.println("Operation thread interrupted...");
            e.printStackTrace();
        }

    }

    /*
    Tester method for config file content
     */
    private void printVars() {
        System.out.println("\nHotel IP: " +hotelAdd+ "\nConcert IP: " +concertAdd);
        System.out.println("Booking file: " + bookingFile.getAbsolutePath());
    }

    public static void main(String[] args){
        Coordinator c = new Coordinator();
        c.printVars();
        //c.closeFiles();
    }
}
