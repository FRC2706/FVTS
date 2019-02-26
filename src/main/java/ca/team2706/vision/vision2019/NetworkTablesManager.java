package ca.team2706.vision.vision2019;

import java.util.ArrayList;
import java.util.List;

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
				
				boolean enabled = params.table.getBoolean("enabled", true);
				
				if(params.enabled == false && enabled && params.table.isConnected()) {
				
					params.enabled = true;
					
					List<MainThread> toRemove = new ArrayList<MainThread>();
					List<MainThread> toAdd = new ArrayList<MainThread>();
					
					for(MainThread thread : Main.threads) {
						
						if(thread.visionParams.name.equals(params.name)) {
							toRemove.add(thread);
							MainThread thread1 = new MainThread(params);
							toAdd.add(thread1);
							thread1.start();
						}
						
					}
					
					for(MainThread thread : toRemove) {
						Main.threads.remove(thread);
					}
					
					for(MainThread thread : toAdd) {
						Main.threads.add(thread);
					}
					
				}else if(params.enabled && !enabled && params.table.isConnected()) {
				
					params.enabled = false;

					for(MainThread thread : Main.threads) {
						
						if(thread.visionParams.name.equals(params.name)) {
							thread.updateParams(params);
							try {
								thread.join();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
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
