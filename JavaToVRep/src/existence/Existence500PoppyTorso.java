package existence;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import coppelia.CharWA;
import coupling.interaction.Interaction030;
import coupling.interaction.Interaction040;

//Run poppy-torso in V-REP: 
// Open V-REP
// poppy-services --vrep --http poppy-torso

public class Existence500PoppyTorso extends Existence050 {

	@Override
	public Interaction040 enactPrimitiveIntearction(Interaction030 intendedPrimitiveInteraction){
		
		String elbow = "r";
		if (intendedPrimitiveInteraction.getLabel().contains(LABEL_E2))
			elbow = "l";
		
		try {
			URL url = new URL("http://127.0.0.1:8080/motor/" + elbow + "_elbow_y/register/goal_position/value.json");	
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoOutput(true); 
	        connection.setInstanceFollowRedirects(false); 
	        connection.setRequestMethod("POST"); 
	        connection.addRequestProperty("d", "0");
	        connection.setRequestProperty("Content-Type", "application/json"); 
	        connection.getResponseCode(); 
	        connection.disconnect(); 		
	    } catch(Exception e) { 
	        throw new RuntimeException(e); 
	    } 

   	
		Interaction040 enactedInteraction = null;
		enactedInteraction = this.addOrGetPrimitiveInteraction(LABEL_E1 +LABEL_R1, 0);

    	return enactedInteraction;
	}	

}
