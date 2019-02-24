package ca.team2706.vision.vision2019;

import ca.team2706.vision.vision2019.Main.VisionParams;

public class NetworkTablesManager extends Thread{
	
	public static void init() {
		new NetworkTablesManager().start();
	}
	
	@Override
	public void run() {
		
		for(VisionParams params : Main.visionParamsList) {
			
			params.table.putBoolean("enabled", params.enabled);
			
		}
		
		while(true) {
			
			for(VisionParams params : Main.visionParamsList) {
				
				params.enabled = params.table.getBoolean("enabled", true);
				
				for(MainThread thread : Main.threads) {
					
					if(thread.visionParams.name.equals(params.name)) {
						thread.updateParams(params);
					}
					
				}
				
			}
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
}
