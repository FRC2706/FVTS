package ca.team2706.vision.trackerboxreloaded;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageDumpScheduler implements Runnable{
	public static final int QUEUE_LIMIT = 10;
	
	public static List<Bundle> bundles = new ArrayList<Bundle>();
	public static Thread thread;
	public static boolean b = true;
	@Override
	public void run() {
		while(b){
			if(bundles.size() > 0){
				Bundle b = bundles.get(0);
				bundles.remove(0);
				while(bundles.size() > QUEUE_LIMIT){
					bundles.remove(0);
				}
				try {
					Main.imgDump(b.getRaw(), "raw",b.getTimeStamp());
					Main.imgDump(b.getBinMask(), "binMask", b.getTimeStamp());
					Main.imgDump(b.getOutput(), "output", b.getTimeStamp());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public static void schedule(Bundle b){
		bundles.add(b);
	}
	public static void start(){
		thread = new Thread(new ImageDumpScheduler());
		thread.start();
	}

}
