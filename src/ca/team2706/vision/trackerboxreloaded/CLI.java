package ca.team2706.vision.trackerboxreloaded;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class CLI implements Runnable, ActionListener {

	private JFrame frame;
	private Socket s;
	private PrintWriter out;
	private Scanner in;
	private JTextField textField;
	private JTextField textField_1;
	private JButton btnConnect;
	private JButton btnSend;
	private JTextArea textArea;
	private static List<String> logs = new ArrayList<String>();
	/**
	 * Create the application.
	 */
	public CLI() {
		initialize();
	}
	public static void log(String message){
		logs.add(message);
		while(logs.size() > 20){
			logs.remove(0);
		}
	}

	public static void startServer(){
		new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					ServerSocket ss = new ServerSocket(6677);
					while(!ss.isClosed()){
						try{
							Socket s = ss.accept();
							new Thread(new Runnable(){
								@Override
								public void run() {
									try {
										PrintWriter out = new PrintWriter(s.getOutputStream(),true);
										Scanner in = new Scanner(s.getInputStream());
										while(!s.isClosed()){
											String next = in.nextLine();
											process(out,in,next);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									
								}
							}).start();
						}catch(Exception e){}
						Thread.sleep(1);
					}
					ss.close();
				}catch(Exception e){
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}).start();
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		new Thread(this).start();
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblIp = new JLabel("IP:");
		lblIp.setBounds(10, 14, 14, 14);
		frame.getContentPane().add(lblIp);
		
		textField = new JTextField();
		textField.setBounds(25, 10, 309, 21);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		btnConnect = new JButton("Connect");
		btnConnect.setBounds(335, 10, 89, 23);
		btnConnect.addActionListener(this);
		frame.getRootPane().setDefaultButton(btnConnect);
		frame.getContentPane().add(btnConnect);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 94, 414, 156);
		frame.getContentPane().add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		textField_1 = new JTextField();
		textField_1.setBounds(10, 75, 324, 20);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		btnSend = new JButton("Send");
		btnSend.setBounds(335, 74, 89, 23);
		btnSend.addActionListener(this);
		frame.getContentPane().add(btnSend);
		
		frame.setVisible(true);
	}

	@Override
	public void run() {
		while(true){
			try{
				if(s != null && in != null && out != null){
					String next = in.nextLine();
					append("remote: "+next);
				}
			}catch(Exception e){
				
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private void append(String message){
		textArea.append(message+System.lineSeparator());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnConnect){
			if(!textField.getText().equals("")){
				try {
					btnConnect.setEnabled(false);
					s = new Socket(textField.getText(),6677);
					in = new Scanner(s.getInputStream());
					out = new PrintWriter(s.getOutputStream(),true);
					btnSend.setEnabled(true);
					frame.getRootPane().setDefaultButton(btnSend);
				} catch (Exception e1) {
					btnConnect.setEnabled(true);
					btnSend.setEnabled(false);
				}
			}
		}
		if(e.getSource() == btnSend){
			out.println(textField_1.getText());
			out.flush();
			append("me: "+textField_1.getText());
			textField_1.setText("");
		}
	}
	public static void process(PrintWriter out, Scanner in, String message){
		if(!message.startsWith("?")){
			help(out);
			return;
		}
		if(message.equalsIgnoreCase("?help")){
			help(out);
			return;
		}
		if(message.equalsIgnoreCase("?reload")){
			Main.loadVisionParams();
			Main.initNetworkTables();
			out.println("Success");
			return;
		}
		if(message.equalsIgnoreCase("?shutdown")){
			out.println("Shutting down!");
			out.flush();
			String os = System.getProperty("os.name");
			if(os.contains("Windows")){
				String[] args = new String[] {"cmd", "/c", "taskkill","/f","/t","/im", "javaw.exe"}; 
				try {
					new ProcessBuilder(args).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				String[] args = new String[] {"/bin/bash", "-c", "killall", "-9", "java"};
				try {
					new ProcessBuilder(args).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if(message.equalsIgnoreCase("?logs")){
			for(String s : logs){
				out.println(s);
			}
			return;
		}
		if(message.equalsIgnoreCase("?restart")){
			String[] args = new String[] {"/bin/bash", "-c", "sudo", "systemctl", "restart","vision.service"};
			try {
				new ProcessBuilder(args).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private static void help(PrintWriter out){
		out.println("Help menu:");
		out.println("?help - shows this menu");
		out.println("?reload - reloads the vision parameters and also networktables");
		out.println("?shutdown - forcively shuts down the vision process");
		out.flush();
	}
}
