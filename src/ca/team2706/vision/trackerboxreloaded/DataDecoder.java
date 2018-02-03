package ca.team2706.vision.trackerboxreloaded;

import java.util.HashMap;

public class DataDecoder {
	
	public static HashMap<String, String> decodeMessage(String message){
		HashMap<String,String> values = new HashMap<String,String>();
		if(message.contains("#")){
			String[] data = message.split("#");
			for(String s : data){
				values.put(s.split(":")[0], s.split(":")[1]);
			}
		}else{
			if(message.contains(":")){
				values.put(message.split(":")[0], message.split(":")[1]);
			}
		}
		return values;
	}
}
