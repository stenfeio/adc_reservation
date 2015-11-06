import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;




public class Hotel extends Thread {
    //TODO add instance variables saving the rooms here.
	public int[] Rooms=new int[10];
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
	void Initialize(){								// Initialize the no. of rooms as 8 for each day
		for(int i=0;i<10;i++){                       
			Rooms[i]=8;
		}
	}
	
	//Check if the reservation of the rooms can be done on particular day or not
		void Reservation(int day){					
			if(Rooms[day]!=0){
				System.out.println("Reservation done");
		Rooms[day]=Rooms[day]-1;
			}
			else
				System.out.println("Room not available for this day");
	}
}

