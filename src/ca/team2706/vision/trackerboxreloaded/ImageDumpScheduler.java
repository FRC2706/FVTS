package ca.team2706.vision.trackerboxreloaded;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageDumpScheduler implements Runnable{
	private static List<Bundle> bundles = new ArrayList<Bundle>();
	private static Thread thread;
	@Override
	public void run() {
		while(true){
			if(bundles.size() > 0){
				Bundle b = bundles.get(bundles.size()-1);
				bundles.remove(bundles.size()-1);
				if(bundles.size() > 5){
					bundles.clear();
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
