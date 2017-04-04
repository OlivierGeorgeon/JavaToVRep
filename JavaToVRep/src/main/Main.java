package main;
import existence.Existence;
import existence.Existence100;
import existence.Existence310;
import existence.Existence400;
import existence.Existence500;

// Make sure to have the server side running in V-REP: 
// in a child script of a V-REP scene, add following command
// to be executed just once, at simulation start:
//
// simExtRemoteApiStart(19999)
//
// then start simulation, and run this program.
//
// IMPORTANT: for each successful call to simxStart, there
// should be a corresponding call to simxFinish at the end!

public class Main
{
	public static void main(String[] args)
    {
    	
		//Existence existence = new Existence100();
		//Existence existence = new Existence310();
		Existence existence = new Existence500();
		
		for(int i = 0 ; i < 300 ; i++){			
			String stepTrace = existence.step();
			System.out.println(i + ": " + stepTrace);
		}
    }
}