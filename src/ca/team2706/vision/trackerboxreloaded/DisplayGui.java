package ca.team2706.vision.trackerboxreloaded;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

/**
 * The Class DisplayGui.
 */
public class DisplayGui extends JFrame implements Runnable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	private int width;
	
	private int height;
	
	/** The image. */
	private BufferedImage image;
	
	/**
	 * Instantiates a new display gui.
	 *
	 * @param image the image
	 */
	public DisplayGui(BufferedImage image, String windowTitle){
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
		this.setPreferredSize(new Dimension(image.getWidth()+30,image.getHeight()+30));
		this.setMinimumSize(new Dimension(image.getWidth()+30,image.getHeight()+30));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setTitle(windowTitle);
		new Thread(this).start();
	}
	
	/**
	 * Instantiates a new display gui.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public DisplayGui(int width, int height, String windowTitle){
		this.width = width;
		this.height = height;
		this.setPreferredSize(new Dimension(width+30,height+30));
		this.setMinimumSize(new Dimension(width+30,height+30));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setTitle(windowTitle);
		new Thread(this).start();
	}
	
	/**
	 * Updates the image to be rendered.
	 *
	 * @param image the image
	 */
	public void updateImage(BufferedImage image){
		this.image = image;
		if(image.getWidth() != width || image.getHeight() != height){
			width = image.getWidth();
			height = image.getHeight();
			this.setPreferredSize(new Dimension(width+30,height+30));
			this.setMinimumSize(new Dimension(width+30,height+30));
			this.setSize(new Dimension(width+30,height+30));
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while(true){
			render();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Renders the latest image.
	 */
	public void render(){
		if(image == null){
			return;
		}
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null){
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		synchronized (image) {
			g.drawImage(image, 0, 0, null);
		}
		g.dispose();
		bs.show();
	}
}
