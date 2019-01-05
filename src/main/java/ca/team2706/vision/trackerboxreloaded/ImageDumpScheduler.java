package ca.team2706.vision.trackerboxreloaded;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageDumpScheduler implements Runnable{
	//public static final int QUEUE_LIMIT = 10;
	
	public static double At = 0;
	public static double Dt = 0;
	private static double lastTime = System.currentTimeMillis();
	
	static List<Bundle> bundles = new ArrayList<Bundle>();
	private static List<Thread> threads = new ArrayList<Thread>();
	@Override
	public void run() {
		while(true){
			synchronized(bundles){
				if(bundles.size() > 0){
					long start = System.currentTimeMillis();
					Bundle b = bundles.get(0);					bundles.remove(0);
					try {
						Main.imgDump(b.getRaw(), "raw",b.getTimeStamp());
						Main.imgDump(b.getBinMask(), "binMask", b.getTimeStamp());
						Main.imgDump(b.getOutput(), "output", b.getTimeStamp());
						long diff = System.currentTimeMillis() - start;
						Dt = (double) diff;
					} catch (IOException e) {
						e.printStackTrace();
					}
				
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					Main.imgDump(b.getRaw(), "raw",b.getTimeStamp());
					Main.imgDump(b.getBinMask(), "binMask", b.getTimeStamp());
					Main.imgDump(b.getOutput(), "output", b.getTimeStamp());
				} catch (Exception e) {
					//Non fatal error
				}
				}
			}
		}
	}
	public static void schedule(Bundle b){
		At = System.currentTimeMillis()-lastTime;
		bundles.add(b);
	}
	public static void start(){
		Thread managerThread = new Thread(new Runnable(){
			
			public void run(){
				
				double threadCount = At/Dt+1;
				int i = 0;
				while(threads.size() > (int) threadCount){
					try {
						threads.get(i).join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					threads.remove(i);
					i++;
				}
				while(threads.size() < (int) threadCount+1){
					Thread thread = new Thread(new ImageDumpScheduler());
					threads.add(thread);
					thread.start();
				}
				try{
					Thread.sleep(1000);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		managerThread.start();
	}

}
