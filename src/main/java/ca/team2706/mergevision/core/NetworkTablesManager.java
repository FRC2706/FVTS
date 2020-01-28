package ca.team2706.mergevision.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.team2706.mergevision.core.params.VisionParams;
import ca.team2706.mergevision.main.Main;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class NetworkTablesManager extends Thread{
	
	public static Map<String,NetworkTable> tables = new HashMap<String,NetworkTable>();
	
	public static void init() {
		new NetworkTablesManager().start();
	}
	
	@Override
	public void run() {
		
		for(VisionParams params : Main.visionParamsList) {
			
			tables.get(params.getByName("name").getValue()).putBoolean("enabled", params.getByName("enabled").getValueB());
			
		}
		
		while(true) {
			
			for(VisionParams params : Main.visionParamsList) {
				NetworkTable table = tables.get(params.getByName("name").getValue());
				
				boolean enabled = table.getBoolean("enabled", true);
				
				if(params.getByName("enabled").getValueB() == false && enabled && table.isConnected()) {
				
					params.getByName("enabled").setValue("true");
					
					List<MainThread> toRemove = new ArrayList<MainThread>();
					List<MainThread> toAdd = new ArrayList<MainThread>();
					
					for(MainThread thread : Main.threads) {
						
						if(thread.visionParams.getByName("name").getValue().equals(params.getByName("name").getValue())) {
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
					
				}else if(params.getByName("enabled").getValueB() && !enabled && table.isConnected()) {
				
					params.getByName("enabled").setValue("false");

					for(MainThread thread : Main.threads) {
						
						if(thread.visionParams.getByName("name").getValue().equals(params.getByName("name").getValue())) {
							thread.updateParams(params);
							try {
								thread.join();
							} catch (InterruptedException e) {
								Log.e(e.getMessage(), true);
							}
						}
						
					}
					
				}
				
				
			}
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Log.e(e.getMessage(), true);
			}
			
		}
		
	}
	
}
