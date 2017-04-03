package existence;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import sun.audio.AudioPlayer;
import agent.Anticipation;
import coppelia.IntW;
import coppelia.remoteApi;
import coupling.Experiment;
import coupling.Experiment040;
import coupling.Result;
import coupling.interaction.Interaction030;
import coupling.interaction.Interaction040;

public class Existence400 extends Existence040 {

    remoteApi vrep;
    int clientID;
    
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
		Experiment040 experiment =  (Experiment040)selectExperiment(anticipations);

		Interaction040 intendedInteraction = experiment.getIntendedInteraction();
		
		Interaction040 enactedInteraction = enact(intendedInteraction);
		System.out.println("Enacted "+ enactedInteraction.toString());
		
		String babySoundFile = "";
		if (intendedInteraction.isPrimitive()) {
			babySoundFile = "sound/baby12.wav"; // pained
			if (enactedInteraction.getValence() > 0)
				babySoundFile = "sound/baby6.wav"; // pleased
		}
		else
		{
			babySoundFile = "sound/baby10.wav"; // Relieved
			if (enactedInteraction.isPrimitive()) {
				babySoundFile = "sound/baby8.wav"; // Fun
			}
		}
    	try {
    		AudioPlayer.player.start(new FileInputStream(babySoundFile));
    	} catch (FileNotFoundException e1) {
    		e1.printStackTrace();
    	}			

		if (enactedInteraction != intendedInteraction && experiment.isAbstract()){
			Result failResult = null;
			if (enactedInteraction.isPrimitive())
				failResult = enactedInteraction.getResult();
			else
				failResult = createOrGetResult(enactedInteraction.getLabel().replace('e', 'E').replace('r', 'R') + ">");
			int valence = enactedInteraction.getValence(); 
			enactedInteraction = (Interaction040)addOrGetPrimitiveInteraction(experiment, failResult, valence);
			System.out.println("Learn failed interaction "+ enactedInteraction.toString());
		}
		

		if (enactedInteraction.getValence() >= 0)
			this.setMood(Mood.PLEASED);
		else
			this.setMood(Mood.PAINED);

		this.learnCompositeInteraction(enactedInteraction);
		
		//this.setPreviousSuperInteraction(this.getLastSuperInteraction());
		this.setEnactedInteraction(enactedInteraction);
		
		return "" + this.getMood();
	}
	
	@Override
	public Interaction040 enact(Interaction030 intendedInteraction){

		
		if (intendedInteraction.isPrimitive()) {
			Interaction040 interaction = (Interaction040)intendedInteraction;
			if (interaction.getExperience().isAbstract())
				// The interaction is primitive but its experiment is abstract
				// TODO handle this better
				return enact(interaction.getExperience().getIntendedInteraction());
			else
				return enactPrimitiveIntearction(intendedInteraction);
		}
		else {			
			// Enact the pre-interaction
			Interaction040 enactedPreInteraction = enact(intendedInteraction.getPreInteraction());
			if (!enactedPreInteraction.equals(intendedInteraction.getPreInteraction()))
				// if the preInteraction failed then the enaction of the intendedInteraction is interrupted here.
				return enactedPreInteraction;
			else{
				// Enact the post-interaction
				String babySoundFile = "sound/baby11.wav"; // Hold your breath
		    	try {
		    		AudioPlayer.player.start(new FileInputStream(babySoundFile));
		    	} catch (FileNotFoundException e1) {
		    		e1.printStackTrace();
		    	}			
				Interaction040 enactedPostInteraction = enact(intendedInteraction.getPostInteraction());
				return (Interaction040)addOrGetCompositeInteraction(enactedPreInteraction, enactedPostInteraction);
			}
		}
	}

	
	@Override
	public Interaction040 enactPrimitiveIntearction(Interaction030 intendedPrimitiveInteraction){
		Experiment experience = intendedPrimitiveInteraction.getExperience();
		System.out.println("Intended primitive interaction: " + intendedPrimitiveInteraction.toString());
		System.out.println("Performing primitive experiment: " + experience.getLabel());

		Result result = returnResult040(experience);
		
		Interaction040 enactedInteraction = (Interaction040)this.addOrGetPrimitiveInteraction(experience, result);		
		/*
		String babySoundFile = "sound/baby12.wav"; // pained
		if (enactedInteraction.getValence() > 0) {
		//if (result.getLabel() == "r2") {
			// pleased
			babySoundFile = "sound/baby6.wav";
		}
    	try {
    		AudioPlayer.player.start(new FileInputStream(babySoundFile));
    	} catch (FileNotFoundException e1) {
    		e1.printStackTrace();
    	}
		*/
		return enactedInteraction;
	}

	@Override
	public Result returnResult040(Experiment experiment){
		int action = 0;
		if (experiment.equals(addOrGetExperience(LABEL_E1)))
			action = 1;
		else if (experiment.equals(addOrGetExperience(LABEL_E2)))
			action = 2;
		else {
			System.out.println("Unknown primitive experiment " + experiment.getLabel());
			System.exit(1);
		}
		
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
