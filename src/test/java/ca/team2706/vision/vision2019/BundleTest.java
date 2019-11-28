package ca.team2706.vision.vision2019;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Test;

import ca.team2706.vision.core.Bundle;

public class BundleTest {

	@Test
	public void getRaw() {
		try {
			BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
			Bundle bundle = new Bundle(image, null, null, 0,null);
			if (!(bundle.getRaw() == image)) {
				fail();
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void getOutput() {
		try {
			BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
			Bundle bundle = new Bundle(null, null, image, 0,null);
			if (!(bundle.getOutput() == image)) {
				fail();
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void getBinMask() {
		try {
			BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
			Bundle bundle = new Bundle(null, image, null, 0,null);
			if (!(bundle.getBinMask() == image)) {
				fail();
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void getTimeStamp() {
		try {
			int i = 99;
			Bundle bundle = new Bundle(null, null, null, i,null);
			if (!(bundle.getTimeStamp() == i)) {
				fail();
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void Bundle() {
		try {
			@SuppressWarnings("unused")
			Bundle bundle = new Bundle(null, null, null, 0,null);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
