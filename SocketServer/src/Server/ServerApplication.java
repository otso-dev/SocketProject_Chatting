package Server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;


public class ServerApplication {
	private final static int PORT = 9090;
	private static SocketServer socketServer;
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(PORT);
			System.out.println("==========<<server start>>==========");
			
			while(true){
				Socket socket = serverSocket.accept();
				//System.out.println(socket.getInetAddress()+":"+ socket.toString());
				//System.out.println("연결확인");
				socketServer = new SocketServer(socket);
				socketServer.start();
			
			}
		} catch (ConnectException e) {
			System.out.println("접속해제");
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(socketServer.getSocket() != null) {
				try {
					socketServer.getSocket().close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				
		}
		
	}
	
	
}
