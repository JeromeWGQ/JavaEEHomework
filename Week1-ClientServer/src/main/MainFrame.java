package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.MyClient;
import server.MyServer;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private int count;

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("\u4E3B\u7A97\u4F53");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 273, 105);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel label = new JLabel("\u670D\u52A1\u5668\u5DF2\u542F\u52A8\uFF0C\u4FE1\u606F\u89C1\u63A7\u5236\u53F0");
		label.setBounds(10, 10, 196, 15);
		contentPane.add(label);

		JButton button = new JButton("\u5F00\u542F\u65B0\u7684\u5BA2\u6237\u7AEF");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							MyClient frame = new MyClient(count++);
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		button.setBounds(10, 35, 168, 23);
		contentPane.add(button);

		count = 0;
		new Thread(new Runnable() {
			public void run() {
				MyServer server = new MyServer();
				server.start();
			}
		}).start();

	}

}
