package existence;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import sun.audio.AudioPlayer;
import agent.Anticipation;
import coupling.Experiment050;
import coupling.interaction.Interaction030;
import coupling.interaction.Interaction040;
import existence.Existence010.Mood;

//Run Roboticia-First in V-REP: 
// Open V-REP
// In the terminal, open a note book: jupyter notebook
// In the browser, open the notebook JavaToVrep/vrep/RoboticiaFirst.ipynb and run it  

public class Existence500RoboticiaFirst extends Existence500 {

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
				Interaction040 enactedPostInteraction = enact(intendedInteraction.getPostInteraction());
				return (Interaction040)addOrGetCompositeInteraction(enactedPreInteraction, enactedPostInteraction);
			}
		}
	}
	@Override
	public Interaction040 enactPrimitiveIntearction(Interaction030 intendedPrimitiveInteraction){
		
		String experimentLabel = LABEL_E1;
		String retultLabel = LABEL_R1;
		if (intendedPrimitiveInteraction.getLabel().contains(LABEL_E2))
			experimentLabel = LABEL_E2;
		
		// Execute the primitive experiment
		try {
			URL url = new URL("http://127.0.0.1:8080/primitive/" + experimentLabel + "/start.json");	
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setInstanceFollowRedirects(false); 
	        connection.setRequestMethod("GET"); 
	        connection.setRequestProperty("Content-Type", "application/json"); 
	        //connection.setDoOutput(true); 
	        
	        int responseCode = connection.getResponseCode(); 
	        //System.out.println("Response Code : " + responseCode);
			Thread.sleep(1500);
	        
	        //connection.disconnect(); 		
	    } catch(Exception e) { 
	        throw new RuntimeException(e); 
	    } 

		// read the result
		try {
			URL url = new URL("http://127.0.0.1:8080/primitive/" + experimentLabel + "/property/result");	
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setInstanceFollowRedirects(false); 
	        connection.setRequestMethod("GET"); 
	        connection.setRequestProperty("Content-Type", "application/json"); 
	        //connection.setDoOutput(true); 
	        
	        int responseCode = connection.getResponseCode(); 
	        //System.out.println("Response Code : " + responseCode);
	        
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine = in.readLine();
			/*
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			System.out.println(response.toString());
			*/
			in.close();		
			String loadString = inputLine.substring(14, 19);
			System.out.println(loadString);
			float presentLoad = Float.parseFloat(loadString);
			
			if (presentLoad > 0){ 	
				retultLabel = LABEL_R2;
			}

			if (retultLabel.equals(LABEL_R2)){
				String babySoundFile = "sound/drum.wav";
				if (experimentLabel.equals(LABEL_E2)){
					babySoundFile = "sound/wood.wav";
				}
				try {
					AudioPlayer.player.start(new FileInputStream(babySoundFile));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}			
			}
			Thread.sleep(1500);
	        //connection.disconnect(); 		
	    } catch(Exception e) { 
	        throw new RuntimeException(e); 
	    } 
		
		Interaction040 enactedInteraction = null;
		enactedInteraction = this.addOrGetPrimitiveInteraction(experimentLabel + retultLabel, 0);

    	return enactedInteraction;
	}	

	/* Method POST
	public Interaction040 enactPrimitiveIntearction(Interaction030 intendedPrimitiveInteraction){
		
		String elbow = "r";
		if (intendedPrimitiveInteraction.getLabel().contains(LABEL_E2))
			elbow = "l";
		
		try {
			URL url = new URL("http://127.0.0.1:8080/motor/" + elbow + "_elbow_y/register/goal_position/value.json");	
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setInstanceFollowRedirects(false); 
	        connection.setRequestMethod("POST"); 
	        connection.setRequestProperty("Content-Type", "application/json"); 
	        
	        connection.setDoOutput(true); 
	        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
	        wr.writeBytes("100");
	        wr.flush();
	        wr.close();
	        
	        int responseCode = connection.getResponseCode(); 
	        
	        System.out.println("Response Code : " + responseCode);
	        
	        //connection.disconnect(); 		
	    } catch(Exception e) { 
	        throw new RuntimeException(e); 
	    } 

   	
		Interaction040 enactedInteraction = null;
		enactedInteraction = this.addOrGetPrimitiveInteraction(LABEL_E1 +LABEL_R1, 0);

    	return enactedInteraction;
	}	*/

}
