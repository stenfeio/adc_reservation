


import java.util.Random;

public class Rooms implements Runnable{
	Thread r;
	
boolean[] AvailRooms=new boolean[10];
public Rooms(){
	r=new Thread(this);
	r.start();
}
public void run(){ 
   for(int i=0;i<10;i++){
	   int j=i+1;
	  Random r= new Random();
	  boolean c=r.nextBoolean();
	  AvailRooms[i]=c;
	  if(AvailRooms[i]==false)
		  System.out.print("Day"+j+"="+"Rooms NOT-available"+"\t");
	  else
		  System.out.print("Day"+j+"="+"Rooms Available"+"\t");  
	
   }
   System.out.println();
 }
}

