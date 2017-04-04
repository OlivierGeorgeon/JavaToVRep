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
import coupling.Experiment050;
import coupling.Result;
import coupling.interaction.Interaction;
import coupling.interaction.Interaction030;
import coupling.interaction.Interaction040;
import existence.Existence010.Mood;

public class Existence500 extends Existence050 {

    remoteApi vrep;
    int clientID;
    
	@Override
	protected void initExistence() {
		//super.initExistence();
		addOrGetPrimitiveInteraction(LABEL_E1 + LABEL_R1, -10);
		Interaction040 i12 = addOrGetPrimitiveInteraction(LABEL_E1 + LABEL_R2, 1);
		addOrGetPrimitiveInteraction(LABEL_E2 + LABEL_R1, -10);
		Interaction040 i22 = addOrGetPrimitiveInteraction(LABEL_E2 + LABEL_R2, 1);
		addOrGetAbstractExperiment(i12);
		addOrGetAbstractExperiment(i22);
		// Initialize the connection with V-Rep
		vrep = new remoteApi();
        vrep.simxFinish(-1); // just in case, close all opened connections
        clientID = vrep.simxStart("127.0.0.1",19999,true,true,5000,5);		       
	}

	@Override
	public String step() {
		
		List<Anticipation> anticipations = anticipate();
		Experiment050 experiment =  (Experiment050)selectExperiment(anticipations);

		Interaction040 intendedInteraction = experiment.getIntendedInteraction();
		System.out.println("Intended "+ intendedInteraction.toString());

		Interaction040 enactedInteraction = enact(intendedInteraction);
		
		if (enactedInteraction != intendedInteraction)
			experiment.addEnactedInteraction(enactedInteraction);

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
			//Interaction040 interaction = (Interaction040)intendedInteraction;
			//if (interaction.getExperience().isAbstract())
				// The interaction is primitive but its experiment is abstract
				// TODO handle this better
				//return enact(interaction.getExperience().getIntendedInteraction());
			//else
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

	/**
	 * Implements the cognitive coupling between the agent and the environment
	 * @param intendedPrimitiveInteraction: The intended primitive interaction to try to enact against the environment
	 * @param The actually enacted primitive interaction.
	 */
	@Override
	public Interaction040 enactPrimitiveIntearction(Interaction030 intendedPrimitiveInteraction){
		int action = 0;
		if (intendedPrimitiveInteraction.getLabel().contains(LABEL_E1))
			action = 1;
		else if (intendedPrimitiveInteraction.getLabel().contains(LABEL_E2))
			action = 2;
		else {
			System.out.println("Unknown primitive interaction " + intendedPrimitiveInteraction.getLabel());
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

		Interaction040 enactedInteraction = null;
    	if (action == 1 && result == 1)
    		enactedInteraction = this.addOrGetPrimitiveInteraction(LABEL_E1 + LABEL_R1, -5);
		else if (action == 1 && result == 2)
    		enactedInteraction = this.addOrGetPrimitiveInteraction(LABEL_E1 + LABEL_R2, 1);
		else if (action == 2 && result == 1)
    		enactedInteraction = this.addOrGetPrimitiveInteraction(LABEL_E2 + LABEL_R1, -5);
		else if (action == 2 && result == 2)
    		enactedInteraction = this.addOrGetPrimitiveInteraction(LABEL_E2 + LABEL_R2, 1);

    	return enactedInteraction;
	}	
	
}