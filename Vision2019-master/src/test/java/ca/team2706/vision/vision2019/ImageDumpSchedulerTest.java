package ca.team2706.vision.vision2019;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Test;

import ca.team2706.vision.vision2019.Bundle;
import ca.team2706.vision.vision2019.ImageDumpScheduler;

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
			ImageDumpScheduler.stop = true;
			BufferedImage image = new BufferedImage(10,10,BufferedImage.TYPE_INT_RGB);
			Bundle bundle = new Bundle(image,image,image,1);
			ImageDumpScheduler.schedule(bundle);
			scheduler.run();
			Bundle bundle1 = new Bundle(null,null,null,2);
			ImageDumpScheduler.schedule(bundle1);
			for(int i = 0; i < ImageDumpScheduler.QUEUE_LIMIT;i++) {
				ImageDumpScheduler.schedule(bundle1);
			}
			scheduler.run();
			ImageDumpScheduler.stop = false;
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
}
