package existence;

import java.io.*;

import sun.audio.*;
import coppelia.IntW;
import coppelia.remoteApi;
import coupling.Experiment;
import coupling.Result;
import coupling.interaction.Interaction;
import existence.Existence010.Mood;


public class Existence100 extends Existence010 {

    remoteApi vrep;
    int clientID;
    //InputStream in;
    
    Result lastResultForExperiment1; 
    Result lastResultForExperiment2; 
	
	@Override
	protected void initExistence() {
		super.initExistence();
		// Initialize the connection with V-Rep
		vrep = new remoteApi();
        vrep.simxFinish(-1); // just in case, close all opened connections
        clientID = vrep.simxStart("127.0.0.1",19999,true,true,5000,5);		       
	}

	@Override
	public String step() {
		
		Experiment experience = this.getPreviousExperiment();
		if (this.getMood() == Mood.BORED){
			experience = getOtherExperience(experience);		
			this.setSelfSatisfactionCounter(0);
		}
		
		Result anticipatedResult = predict(experience);
		
		Result result = returnResult010(experience);
	
		if (experience.equals(addOrGetExperience(LABEL_E1)))
			lastResultForExperiment1 = result;
		else
			lastResultForExperiment2 = result;

		this.addOrGetPrimitiveInteraction(experience, result);
		
		if (result == anticipatedResult){
			this.setMood(Mood.SELF_SATISFIED);
			this.incSelfSatisfactionCounter();
		}
		else{
			this.setMood(Mood.FRUSTRATED);
			this.setSelfSatisfactionCounter(0);
		}
		if (this.getSelfSatisfactionCounter() >= BOREDOME_LEVEL)
			this.setMood(Mood.BORED);
		
		this.setPreviousExperiment(experience);

		String babySoundFile = "sound/baby1.wav";
		switch (getMood()) {
		case SELF_SATISFIED:
			babySoundFile = "sound/baby6.wav";
			break;
		case FRUSTRATED:
			babySoundFile = "sound/baby8.wav";
			break;
		case PAINED:
		case PLEASED:
		case BORED:
			babySoundFile = "sound/baby11.wav";
			break;
		}
    	try {
    		AudioPlayer.player.start(new FileInputStream(babySoundFile));
    	} catch (FileNotFoundException e1) {
    		e1.printStackTrace();
    	}
    	
		
		return experience.getLabel() + result.getLabel() + " " + this.getMood();
	}

	/**
	 * Predicts the expected result for a given experiment
	 * @return The expected result.
	 */
	@Override
	protected Result predict(Experiment experience){
		Result anticipatedResult = null;
		if (experience.equals(addOrGetExperience(LABEL_E1)))
			anticipatedResult = lastResultForExperiment1;
		else
			anticipatedResult = lastResultForExperiment2;
		
		return anticipatedResult;
	}
	/**
	 * The Environment010
	 * E1 results in R1. E2 results in R2.
	 * @param experiment: The current experiment.
	 * @return The result of this experiment.
	 */
	@Override
	public Result returnResult010(Experiment experiment){
		int action = 1;
		if (experiment.equals(addOrGetExperience(LABEL_E2)))
			action = 2;
		
    	// Send the action signal to V-Rep. 
    	// V-Rep will reset the action signal after reading it
    	vrep.simxSetIntegerSignal(clientID, "action", action, vrep.simx_opmode_oneshot);

    	// Read the result signal 
    	IntW signalValue = new IntW(0);
    	while (signalValue.getValue() == 0)
    	{
    		vrep.simxGetIntegerSignal(clientID, "result", signalValue, vrep.simx_opmode_blocking);
    	}
		int result = signalValue.getValue();
		
    	// Reset the result signal after reading it
    	vrep.simxSetIntegerSignal(clientID, "result", 0, vrep.simx_opmode_blocking);
    	
    	System.out.format("Action: %d, Result: %d\n", action, result); 

    	Result resultObject; 
    	if (result == 1)
			resultObject =  createOrGetResult(LABEL_R1);
		else
			resultObject =  createOrGetResult(LABEL_R2);

    	return resultObject;
	}	
}
