package existence;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import coupling.interaction.Interaction030;
import coupling.interaction.Interaction040;

//Run poppy-torso in V-REP: 
// Open V-REP
// poppy-services --vrep --http poppy-torso

public class Existence500PoppyTorso extends Existence500 {

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
			Thread.sleep(3000);
	        
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
			String loadString = inputLine.substring(14, 16);
			System.out.println(loadString);
			int presentLoad = Integer.valueOf(loadString);
			
			if (presentLoad > 0) 	
				retultLabel = LABEL_R2;
	        
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
