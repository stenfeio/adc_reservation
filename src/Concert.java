import java.util.Random;

public class Concert implements Runnable {

	boolean[] ConcertDays= new boolean[10];
	Thread c;

    public Concert(){
		c=new Thread(this);
		c.start();
	}

    public void run(){
		for(int i=0;i<10;i++){
			  Random r= new Random();
			  boolean c=r.nextBoolean();
			  ConcertDays[i]=c;
			  int j=i+1;
			  if(ConcertDays[i]==false)
				  System.out.print("Day"+j+"="+"NO-concert"+"\t");
			  else
				  System.out.print("Day"+j+"="+"Concert"+"\t");
		   }
		System.out.println();
	}
}
