package ca.team2706.vision.trackerboxreloaded;

import java.util.HashMap;

public class DataUtils {
	
	/**Decodes the data from a String into a HashMap
	 * :]
	 * @param data 
	 * @return
	 */
	public static HashMap<String, String> decodeData(String data){
		HashMap<String,String> values = new HashMap<String,String>();
		if(data.contains("#")){
			String[] data2 = data.split("#");
			for(String s : data2){
				if(s.contains(":")){
					values.put(s.split(":")[0], s.split(":")[1].replaceAll("&ps", "#"));
				}
			}
		}else{
			if(data.contains(":")){
				values.put(data.split(":")[0], data.split(":")[1].replaceAll("&ps", "#"));
			}
		}
		return values;
	}
	/**Encodes a HashMap of data into a String!
	 * :]
	 * 
	 * @param raw data
	 * @return encoded data
	 */
	public static String encodeData(HashMap<String,String> data){
		String output = "";
		if(data.isEmpty()){
			return output;
		}
		for(String key : data.keySet()){
			output += key+":"+data.get(key).replaceAll("#", "&ps")+"#";
		}
		if(output.endsWith("#")){
			output = output.substring(0, output.length()-1);
		}
		return output;
	}
}
