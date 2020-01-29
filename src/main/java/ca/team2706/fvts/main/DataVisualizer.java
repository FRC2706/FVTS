package ca.team2706.fvts.main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import ca.team2706.fvts.core.Constants;

public class DataVisualizer extends JFrame{

	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) throws Exception {
		new DataVisualizer();
	}
	@SuppressWarnings("resource")
	public DataVisualizer() throws Exception {
		setSize(640, 480);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		System.out.println("FVTS DataVisualizer " + Constants.VERSION_STRING + " developed by " + Constants.AUTHOR);

		Scanner in = new Scanner(System.in);
		Color c = Color.RED;
		System.out.print("Image file path: ");
		BufferedImage image = ImageIO.read(new File(in.nextLine()));
		final int dotSizeAt640 = 16;
		int currX = -1;
		while(true) {
			render(image);
			System.out.print("Type (0=re-render 1=ctrX 2=area 3=angle c=colour):");
			String input = in.nextLine();
			if(input.equalsIgnoreCase("c")) {
				System.out.print("Colour: ");
				c = Color.getColor(in.nextLine());
			} else {
				int selection;
				try {
					selection = Integer.valueOf(input);
				}catch(NumberFormatException e) {
					System.err.println("That is not a valid selection!");
					continue;
				}
				switch(selection) {
				case 1:
					System.out.print("ctrX: ");
					double ctrX = Double.valueOf(in.nextLine());
					int widthTmp = image.getWidth()/2;
					int x = (int) (ctrX * widthTmp + widthTmp);
					currX = x;
					
					Graphics g = image.getGraphics();
					g.setColor(c);
					int size = image.getWidth()/640*dotSizeAt640;
					g.fillOval(x, image.getHeight()/2, size, size);
					g.dispose();
					
					break;
				case 2:
					if(currX == -1) {
						System.err.println("A centre X must be provided before the area can be applied");
						continue;
					}
					System.out.print("Area: ");
					double area = Double.valueOf(in.nextLine());
					int actualArea = (int) ((image.getWidth()*image.getHeight()) * area);
					int approxSide = (int) Math.sqrt(actualArea);
					
					g = image.getGraphics();
					g.setColor(c);
					int y = image.getHeight()/2-approxSide/2;
					g.drawRect(currX-approxSide/2, y, approxSide, approxSide);
					g.dispose();
					
					break;
				case 3:
					System.out.print("Angle: ");
					double angle = Double.valueOf(in.nextLine());
					ctrX = angle/45;
					widthTmp = image.getWidth()/2;
					x = (int) (ctrX * widthTmp + widthTmp);
					currX = x;
					
					g = image.getGraphics();
					g.setColor(c);
					size = image.getWidth()/640*dotSizeAt640;
					g.fillOval(x, image.getHeight()/2, size, size);
					g.dispose();
					break;
				}
			}
		}
	}
	private void render(BufferedImage image) {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			bs = getBufferStrategy();
		}
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		
		g.drawImage(image, 0,0, null);
		
		g.dispose();
		bs.show();
	}
}
