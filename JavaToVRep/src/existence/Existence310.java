package existence;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import sun.audio.AudioPlayer;
import agent.Anticipation;
import coppelia.IntW;
import coppelia.remoteApi;
import coupling.Experiment;
import coupling.Result;
import coupling.interaction.Interaction031;
import existence.Existence010.Mood;

public class Existence310 extends Existence031 {

    remoteApi vrep;
    int clientID;
    
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
		
		List<Anticipation> anticipations = anticipate();
		Experiment experience =  selectExperiment(anticipations);
		
		Result result = returnResult031(experience);
	
		Interaction031 enactedInteraction = getInteraction(experience.getLabel() + result.getLabel());
		System.out.println("Enacted "+ enactedInteraction.toString());

		if (enactedInteraction.getValence() >= 0)
			this.setMood(Mood.PLEASED);
		else
			this.setMood(Mood.PAINED);
		
		this.learnCompositeInteraction(enactedInteraction);
		
		this.setEnactedInteraction(enactedInteraction);
		
		String babySoundFile = "sound/baby1.wav";
		switch (getMood()) {
		case SELF_SATISFIED:
			babySoundFile = "sound/baby6.wav";
			break;
		case FRUSTRATED:
			babySoundFile = "sound/baby8.wav";
			break;
		case PAINED:
			babySoundFile = "sound/baby12.wav";
			break;
		case PLEASED:
			babySoundFile = "sound/baby6.wav";
			break;
		case BORED:
			babySoundFile = "sound/baby11.wav";
			break;
		}
    	try {
    		AudioPlayer.player.start(new FileInputStream(babySoundFile));
    	} catch (FileNotFoundException e1) {
    		e1.printStackTrace();
    	}

		
		return "" + this.getMood();
	}

	@Override
	public Result returnResult031(Experiment experiment){
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
