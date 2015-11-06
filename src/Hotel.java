import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.*;

import Coordinator.Status;
import CoordinatorThreads.OperationThread;


public class Hotel  {
    //TODO add instance variables saving the rooms here.
	public int[] Rooms=new int[10];
	int day;
	String CoordinatorAdd;     //Address for Coordinator
	//path to resources
	 public enum Status{
	        NORMAL, RECOVERY, FAILED
	    }
    final String resourcePath = "C:\\Users\\Talal\\IdeaProjects\\adc_reservation\\resources\\";
    Status SYSTEM_STATUS;               //System status variable


    File recoveryFile;                  //Defines recovery file path
    BufferedWriter recoveryFileWriter;  //Defines recover file writer

    File configurationFile;				//Defines hotel configuration path
    BufferedReader configurationFileReader;    //Defines hotel configuration reader
	/*public Hotel(Rooms p1, Concert p2){

        for(int i=0;i<10;i++){
			int j=i+1;
			if(p1.AvailRooms[i]==true && p2.ConcertDays[i]==true){
				System.out.println("Reservation can be done on day"+j);
			}
		}
	}*/
    public Hotel(){
        SYSTEM_STATUS = Status.FAILED;
        configurationFile = new File(resourcePath+ "hotel-configuration-file.txt");
        recoveryFile = new File(resourcePath+ "coordinator-recovery.txt");

        openFiles(configurationFile, recoveryFile);
        Initialization();							// Initialize the no. of rooms as 8 for each day
        //Reservation(day);							// pass the day on which the reservation has to be done(this user will provide)?
       	
        HotelThread threadManager = new HotelThread();
        HotelThread.OperationThread ot = threadManager.new OperationThread();
        ot.run();
    	
    	
    }
    private void closeFiles(){
        try{
            if (configurationFileReader != null)
                configurationFileReader.close();
         
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
    private void Initialization(){
        String line;
        System.out.println("Reading from config file...");
        if (configurationFileReader != null) {
            try {
                line = configurationFileReader.readLine();							//reads the address of the coordinator given in file
                CoordinatorAdd= line;
                for(int i=0;i<9;i++){
                	String[] temp;													//String split to read the no. of rooms from file
                line = configurationFileReader.readLine();
                String delimiter = " ";
                temp=line.split(delimiter);
                Rooms[i]=Integer.valueOf(temp[1]);		                              //Initialize the rooms with the value given in file
               // concertAdd = line;
                }

                line = configurationFileReader.readLine();

                configurationFileReader.close();
                System.out.println("Finished setting ip addresses...\nConfigFileReader closing");

            }catch (Exception e){
                System.err.println("Issue with setting addresses...");
                e.printStackTrace();
            }
        }
    }
    /*
     * Helper method to open files. Throws exceptions if files not found.
     * @param Hotel-configurationFile
     * @param recoveryFile
     */
    private void openFiles(File configurationFile, File recoveryFile){
        try{
            configurationFileReader = new BufferedReader(new FileReader(configurationFile));
            recoveryFileWriter = new BufferedWriter(new FileWriter(recoveryFile));

        }catch(FileNotFoundException e){
            System.err.println("Could not find configuration file...");
            e.printStackTrace();
        }catch (IOException e){
            System.err.println("Can't write to recovery file...");
            e.printStackTrace();
        }
    }

	
	//Check if the reservation of the rooms can be done on particular day or not
		void Reservation(int day){						// day for which reservation to be done
			
			if(Rooms[day]!=0){
				System.out.println("Reservation done");
		Rooms[day]=Rooms[day]-1;
			}
			else
				System.out.println("Room not available for this day");
	}
}

