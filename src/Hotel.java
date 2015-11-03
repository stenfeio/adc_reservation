
public class Hotel {

	public Hotel(Rooms p1, Concert p2){
		for(int i=0;i<10;i++){
			int j=i+1;
			if(p1.AvailRooms[i]==true && p2.ConcertDays[i]==true){
				System.out.println("Reservation can be done on day"+j);
			}
		}
	
	}
}
