// Copyright 2006-2016 Coppelia Robotics GmbH. All rights reserved. 
// marc@coppeliarobotics.com
// www.coppeliarobotics.com
// 
// -------------------------------------------------------------------
// THIS FILE IS DISTRIBUTED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
// WARRANTY. THE USER WILL USE IT AT HIS/HER OWN RISK. THE ORIGINAL
// AUTHORS AND COPPELIA ROBOTICS GMBH WILL NOT BE LIABLE FOR DATA LOSS,
// DAMAGES, LOSS OF PROFITS OR ANY OTHER KIND OF LOSS WHILE USING OR
// MISUSING THIS SOFTWARE.
// 
// You are free to use/modify/distribute this file for whatever purpose!
// -------------------------------------------------------------------
//
// This file was automatically created for V-REP release V3.3.2 on August 29th 2016
package main;
import coppelia.IntW;
import coppelia.remoteApi;

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
    @SuppressWarnings("static-access")
	public static void main(String[] args)
    {
    	int action = 1;
    	int result = 2;
        System.out.println("Program started");
        remoteApi vrep = new remoteApi();
        vrep.simxFinish(-1); // just in case, close all opened connections
        int clientID = vrep.simxStart("127.0.0.1",19999,true,true,5000,5);
        if (clientID!=-1)
        {
            System.out.println("Connected to remote API server");   

            Boolean condition = true;
            while (condition) {
            	
            	if (result == 2)  {	
            		if (action == 1) action = 2;
            		else action = 1;
            	}
            
            	// Send the action signal to V-Rep. 
            	// V-Rep will reset the action signal after reading it
            	vrep.simxSetIntegerSignal(clientID, "action", action, vrep.simx_opmode_oneshot);

            	// Now retrieve streaming data (i.e. in a non-blocking fashion):
            	//long startTime=System.currentTimeMillis();
            	IntW signalValue = new IntW(0);
            	while (signalValue.getValue() == 0)
            	{
            		vrep.simxGetIntegerSignal(clientID, "result", signalValue, vrep.simx_opmode_blocking);
            	}
        		result = signalValue.getValue();    
            	
            	// Reset the result signal after reading it
            	vrep.simxSetIntegerSignal(clientID, "result", 0, vrep.simx_opmode_blocking);
            	
            	System.out.format("Action: %d, Result: %d\n",action, result); 

            }
            
            // Before closing the connection to V-REP, make sure that the last command sent out had time to arrive. You can guarantee this with (for example):
            IntW pingTime = new IntW(0);
            vrep.simxGetPingTime(clientID,pingTime);

            // Now close the connection to V-REP:   
            vrep.simxFinish(clientID);
        }
        else
            System.out.println("Failed connecting to remote API server");
        System.out.println("Program ended");
    }
}
            
