package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MyServer {

	private static final int PORT = 3333;
	private ArrayList<WorkThread> listThread;

	public void start() {
		listThread = new ArrayList<WorkThread>();
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("Started:" + serverSocket);
			while (true) {
				Socket socket = serverSocket.accept();
				WorkThread newThread = new WorkThread(socket, listThread.size());
				newThread.start();
				System.out.println("New Thread Started.(" + listThread.size() + ")");
				listThread.add(newThread);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class WorkThread extends Thread {

		private Socket socket;
		private int id;

		public WorkThread(Socket socket, int id) {
			this.socket = socket;
			this.id = id;
		}

		@Override
		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				PrintStream ps = new PrintStream(socket.getOutputStream());
				while (true) {
					String receive = br.readLine();
					if (receive == null) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						continue;
					}
					System.out.println("Received from client " + id + ": " + receive);
					String reString = reverse(receive);
					ps.println(reString);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private String reverse(String str) {
			int length = str.length();
			char[] result = new char[length];
			for (int i = 0; i < length; i++) {
				result[i] = str.charAt(length - i - 1);
			}
			String re = new String(result);
			return re;
		}

	}

}
