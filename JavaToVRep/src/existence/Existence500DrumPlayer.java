package existence;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;


import sun.audio.AudioPlayer;
import coupling.interaction.Interaction030;
import coupling.interaction.Interaction040;

public class Existence500DrumPlayer extends Existence500AuClairDeLaLune {
	
    private String fileIntended = System.getProperty("user.home")+"/git/JavaToVRep/JavaToVRep/vrep/intended.txt";
    private String fileEnacted  = System.getProperty("user.home")+"/git/JavaToVRep/JavaToVRep/vrep/enacted.txt";

    public int result;


	@Override
	public Interaction040 enactPrimitiveIntearction(Interaction030 intendedPrimitiveInteraction){
		
		String experimentLabel = LABEL_E1;
		String retultLabel = LABEL_R1;
		if (intendedPrimitiveInteraction.getLabel().contains(LABEL_E2))
			experimentLabel = LABEL_E2;
		if (intendedPrimitiveInteraction.getLabel().contains(LABEL_E3))
			experimentLabel = LABEL_E3;
		
		
        // write intended interaction
       try{
           PrintWriter file  = new PrintWriter(new FileWriter(fileIntended));
           file.print(experimentLabel);
           file.close();
       }
       catch (IOException e) {e.printStackTrace();}
      
       // get enacted interaction
       try{
           String line=null;
           while (line==null){
               try{Thread.currentThread();
                   Thread.sleep(200);}
               catch(Exception ie){}
               
               InputStream ips=new FileInputStream(fileEnacted);
               InputStreamReader ipsr=new InputStreamReader(ips);
               BufferedReader br=new BufferedReader(ipsr);
               line=br.readLine();   
               br.close();
           }
           PrintWriter file  = new PrintWriter(new FileWriter(fileEnacted));
           file.print("");
           file.close();
           retultLabel = line;
       }
       catch (IOException e) {e.printStackTrace();}

       // read the result
		try {
			Thread.sleep(1000);
	    } catch(Exception e) { 
	        throw new RuntimeException(e); 
	    } 
		
		Interaction040 enactedInteraction = null;
		enactedInteraction = this.addOrGetPrimitiveInteraction(experimentLabel + retultLabel, 0);

    	return enactedInteraction;
	}	

}
