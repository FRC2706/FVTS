package ca.team2706.vision.trackerboxreloaded;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

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
	/**
	 * Create the application.
	 */
	public CLI() {
		initialize();
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
		frame.getContentPane().add(btnConnect);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 94, 414, 156);
		frame.getContentPane().add(scrollPane);
		
		textArea = new JTextArea();
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
					append(in.nextLine());
				}
				if(s == null){
					throw new Exception();
				}
			}catch(Exception e){
				s = null;
				in = null;
				out = null;
				btnConnect.setEnabled(true);
				btnSend.setEnabled(false);
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
				} catch (Exception e1) {
					btnConnect.setEnabled(true);
					btnSend.setEnabled(false);
				}
			}
		}
		if(e.getSource() == btnSend){
			out.println(textField_1.getText());
		}
	}
}
