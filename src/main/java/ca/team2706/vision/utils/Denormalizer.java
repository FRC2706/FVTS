package ca.team2706.vision.utils;

import java.io.File;
import java.util.Scanner;

import ca.team2706.vision.core.Utils;
import ca.team2706.vision.core.params.VisionParams;
import ca.team2706.vision.vision2019.Main;

public class Denormalizer {
	public static void main(String[] args) {
		Main.visionParamsFile = new File("visionParams.properties");
		Utils.loadVisionParams();
		Scanner in = new Scanner(System.in);
		while(true) {
			System.out.print("Profile name (or END to exit): ");
			String name = in.nextLine();
			if(name.equalsIgnoreCase("END"))
				break;
			VisionParams params;
			try {
				params = findVisionParams(name);
			} catch (Exception e1) {
				System.err.println("Invalid profile name!");
				continue;
			}
			System.out.println("Types:\n1=normX to X\n2=normY to Y\n3=x to normX\n4=y to normY\n5=normArea to area\n6=area to normArea");
			System.out.print("Conversion type: ");
			String type = in.nextLine();
			int value;
			try {
				value = Integer.valueOf(type);
			}catch(NumberFormatException e) {
				System.err.println("Please enter a number!");
				continue;
			}
			switch(value) {
			case 1:
				System.out.print("Normalized X: ");
				String input = in.nextLine();
				double iInput;
				try{
					iInput = Double.valueOf(input);
				}catch(NumberFormatException e) {
					System.err.println("Please enter a number!");
					continue;
				}
				int widthTmp = params.getByName("width").getValueI()/2;
				int x = (int) (iInput * widthTmp + widthTmp);
				System.out.println("Denormalized X is "+x);
				break;
			case 2:
				System.out.print("Normalized Y: ");
				input = in.nextLine();
				try{
					iInput = Double.valueOf(input);
				}catch(NumberFormatException e) {
					System.err.println("Please enter a number!");
					continue;
				}
				int heightTmp = params.getByName("height").getValueI()/2;
				int y = (int) (iInput * heightTmp + heightTmp);
				System.out.println("Denormalized Y is "+y);
				break;
			case 3:
				System.out.print("X: ");
				input = in.nextLine();
				try{
					iInput = Integer.valueOf(input);
				}catch(NumberFormatException e) {
					System.err.println("Please enter a number!");
					continue;
				}
				widthTmp = params.getByName("width").getValueI()/2;
				double normX = (iInput - widthTmp) / widthTmp;
				System.out.println("Normalized X is "+normX);
				break;
			case 4:
				System.out.print("Y: ");
				input = in.nextLine();
				try{
					iInput = Integer.valueOf(input);
				}catch(NumberFormatException e) {
					System.err.println("Please enter a number!");
					continue;
				}
				heightTmp = params.getByName("height").getValueI()/2;
				double normY = (iInput - heightTmp) / heightTmp;
				System.out.println("Normalized Y is "+normY);
				break;
			case 5:
				System.out.print("normArea: ");
				input = in.nextLine();
				try{
					iInput = Double.valueOf(input);
				}catch(NumberFormatException e) {
					System.err.println("Please enter a number!");
					continue;
				}
				widthTmp = params.getByName("width").getValueI();
				heightTmp = params.getByName("height").getValueI();
				int profileArea = widthTmp * heightTmp;
				int area = (int) (iInput * profileArea);
				System.out.println("Area is "+area);
				break;
			case 6:
				System.out.print("Area: ");
				input = in.nextLine();
				try{
					iInput = Integer.valueOf(input);
				}catch(NumberFormatException e) {
					System.err.println("Please enter a number!");
					continue;
				}
				widthTmp = params.getByName("width").getValueI();
				heightTmp = params.getByName("height").getValueI();
				profileArea = widthTmp * heightTmp;
				double normArea = iInput / profileArea;
				System.out.println("Normalized area is "+normArea);
				break;
			}
		}
		in.close();
	}
	private static VisionParams findVisionParams(String name) throws Exception{
		for(VisionParams params : Main.visionParamsList) {
			if(params.getByName("name").getValue().equals(name))
				return params;
		}
		throw new Exception();
	}
}
