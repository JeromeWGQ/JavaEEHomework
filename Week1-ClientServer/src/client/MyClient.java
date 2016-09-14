package client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class MyClient extends JFrame {

	private static final int PORT = 3333;

	private Socket socket;
	private InputStreamReader isr;
	private BufferedReader br;
	private PrintStream ps;

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Create the frame.
	 */
	public MyClient(int count) {
		setBounds(100, 100, 450, 142);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textField = new JTextField();
		textField.setBounds(10, 10, 297, 21);
		contentPane.add(textField);
		textField.setColumns(10);

		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				click();
			}
		});
		btnSubmit.setBounds(331, 9, 93, 23);
		contentPane.add(btnSubmit);

		JLabel lblReceive = new JLabel("Receive:");
		lblReceive.setBounds(20, 41, 54, 15);
		contentPane.add(lblReceive);

		textField_1 = new JTextField();
		textField_1.setEditable(false);
		textField_1.setBounds(10, 66, 414, 21);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		setTitle(count + "");

		try {
			socket = new Socket("127.0.0.1", PORT);
			isr = new InputStreamReader(socket.getInputStream());
			br = new BufferedReader(isr);
			ps = new PrintStream(socket.getOutputStream());
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void click() {
		try {
			String text = textField.getText();
			ps.println(text);
			String reString;
			reString = br.readLine();
			textField_1.setText(reString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
