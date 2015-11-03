

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Concert C= new Concert();
		
		Rooms R=new Rooms();
		
		try{
			R.r.join();
			C.c.join();
		}
		catch(InterruptedException e)
		{
			System.out.println("Inttrrpted");
		}
		Hotel H=new Hotel(R,C);
	}

}
