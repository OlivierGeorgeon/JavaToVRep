package main;
import existence.Existence;
import existence.Existence100;
import existence.Existence310;
import existence.Existence400;
import existence.Existence500;
import existence.Existence500PoppyTorso;

public class Main
{
	public static void main(String[] args)
    {
    	
		//Existence existence = new Existence100();
		//Existence existence = new Existence310();
		Existence existence = new Existence500PoppyTorso();
		
		for(int i = 0 ; i < 100 ; i++){			
			String stepTrace = existence.step();
			System.out.println(i + ": " + stepTrace);
		}
    }
}