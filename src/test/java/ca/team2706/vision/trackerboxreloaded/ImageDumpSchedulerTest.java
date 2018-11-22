package ca.team2706.vision.trackerboxreloaded;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Test;

public class ImageDumpSchedulerTest {

	@Test
	public void schedule() {
		try {
			
			BufferedImage image = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
			
			ImageDumpScheduler.b = false;
			
			ImageDumpScheduler.schedule(new Bundle(image,image,image,0));
			
			ImageDumpScheduler.bundles.clear();
			
			ImageDumpScheduler.b = true;
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	@Test
	public void start() {
		try {
			
			ImageDumpScheduler.start();
			
			ImageDumpScheduler.b = false;
			
			ImageDumpScheduler.thread.join();
			
			ImageDumpScheduler.b = true;
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	@Test
	public void run() {
		try {
			
			ImageDumpScheduler scheduler = new ImageDumpScheduler();
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ImageDumpScheduler.b = false;
				}
				
			}).start();
			BufferedImage image = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
			Bundle bundle = new Bundle(image,image,image,1);
			ImageDumpScheduler.schedule(bundle);
			scheduler.run();
			
			ImageDumpScheduler.b = true;
			
		}catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
}
