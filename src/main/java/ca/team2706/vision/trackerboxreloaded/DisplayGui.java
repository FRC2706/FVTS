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
	
	/**
	 * The width of the image
	 */
	private int width;
	/**
	 * The height of the image
	 */
	private int height;
	
	/** The image. */
	private BufferedImage image;
	
	/**
	 * Instantiates a new display gui.
	 *
	 * @param image the image
	 */
	public DisplayGui(BufferedImage image, String windowTitle){
		//Sets the image to the image
		this.image = image;
		//Sets the width to the image width
		this.width = image.getWidth();
		//Sets the height to the image height
		this.height = image.getHeight();
		//Sets the preffered size to the image width and image height + 30
		this.setPreferredSize(new Dimension(image.getWidth()+30,image.getHeight()+30));
		//Sets the minimum size to the image width and image height + 30
		this.setMinimumSize(new Dimension(image.getWidth()+30,image.getHeight()+30));
		//Makes the program exit when the X button on the window is pressed
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Makes the window visible
		this.setVisible(true);
		//Sets the window title
		this.setTitle(windowTitle);
		//Starts the rendering thread
		new Thread(this).start();
	}
	
	/**
	 * Instantiates a new display gui.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public DisplayGui(int width, int height, String windowTitle){
		//Sets the width to the width
		this.width = width;
		//Sets the height to the height
		this.height = height;
		//Sets the preffered size to the width and height + 30
		this.setPreferredSize(new Dimension(width+30,height+30));
		//Sets the minimum size to the width and height + 30
		this.setMinimumSize(new Dimension(width+30,height+30));
		//Makes the program end when the X button on the window is clicked
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Makes the window visible
		this.setVisible(true);
		//Sets the title to the window title
		this.setTitle(windowTitle);
		//Starts the rendering thread
		new Thread(this).start();
	}
	
	/**
	 * Updates the image to be rendered.
	 *
	 * @param image the image
	 */
	public void updateImage(BufferedImage image){
		//Sets the image to the image
		this.image = image;
		//If the image width or height is more or less than the last image change the size
		if(image.getWidth() != width || image.getHeight() != height){
			//Sets the width to the image width
			width = image.getWidth();
			//Sets the height to the image height
			height = image.getHeight();
			//Sets the preffered size to the width and height + 30
			this.setPreferredSize(new Dimension(width+30,height+30));
			//Sets the minimum size to the width and height + 30
			this.setMinimumSize(new Dimension(width+30,height+30));
			//Sets the size to the width and height + 30
			this.setSize(new Dimension(width+30,height+30));
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while(true){
			//Renders the window
			render();
			try {
				//Sleep for 1ms
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
		//If there is no image, return
		if(image == null){
			return;
		}
		//Gets the image Buffer Strategy
		BufferStrategy bs = this.getBufferStrategy();
		//If there is no Buffer Strategy
		if(bs == null){
			//Make 3 buffer strategys
			this.createBufferStrategy(3);
			return;
		}
		//Gets the graphics
		Graphics g = bs.getDrawGraphics();
		
		//Sets the color to white
		g.setColor(Color.WHITE);
		//Fills in the background
		g.fillRect(0, 0, getWidth(), getHeight());
		
		synchronized (image) {
			//Draws the image
			g.drawImage(image, 0, 0, null);
		}
		//Clean up
		g.dispose();
		//Show the image
		bs.show();
	}
}
