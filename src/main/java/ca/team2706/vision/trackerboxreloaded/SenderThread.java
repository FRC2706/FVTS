package ca.team2706.vision.trackerboxreloaded;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;

public class SenderThread extends Thread{
	public static final int PORT = 1190;

	private Map<InetAddress, Integer> ips = new HashMap<InetAddress, Integer>();

	@Override
	public void run() {

		try {
			new Thread(new Runnable() {

				@SuppressWarnings("resource")
				@Override
				public void run() {
					try {
						DatagramSocket datagramSocket = new DatagramSocket();

						while (true) {
							for (InetAddress i : ips.keySet()) {
								int id = ips.get(i);
								Mat frame = CameraServer.getFrame(id);

								try {
									BufferedImage image = Main.matToBufferedImage(frame);

									ByteArrayOutputStream out = new ByteArrayOutputStream();

									ImageIO.write(image, "PNG", out);

									byte[] data = out.toByteArray();
									
									DatagramPacket packet = new DatagramPacket(data,data.length, i, PORT);

									datagramSocket.send(packet);
									
								} catch (IOException e) {
									e.printStackTrace();
								}

							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}).start();
			ServerSocket ss = new ServerSocket(PORT);

			while (!ss.isClosed()) {
				try {
					final Socket s = ss.accept();
					final Scanner in = new Scanner(s.getInputStream());
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								int id = Integer.valueOf(in.nextLine());
								ips.put(s.getInetAddress(), id);
								in.close();
								s.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}).start();
				} catch (Exception e) {

				}
			}

			ss.close();

		} catch (Exception e) {
			// This is bad!!!
			e.printStackTrace();
		}

	}

}
