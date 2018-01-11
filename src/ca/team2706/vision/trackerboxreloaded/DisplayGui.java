package ca.team2706.vision.trackerboxreloaded;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class DisplayGui extends JFrame implements Runnable {
	
	private static final long serialVersionUID = 1L;
	
	private BufferedImage image = null;
	public DisplayGui(BufferedImage image){
		this.image = image;
		this.setPreferredSize(new Dimension(image.getWidth()+30,image.getHeight()+30));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		new Thread(this).start();
	}
	public DisplayGui(int width, int height){
		this.setPreferredSize(new Dimension(width+30,height+30));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		new Thread(this).start();
	}
	public void updateImage(BufferedImage image){
		this.image = image;
	}

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
		synchronized (image) {
			g.drawImage(image, 0, 0, null);
		}
		g.dispose();
		bs.show();
	}
}
