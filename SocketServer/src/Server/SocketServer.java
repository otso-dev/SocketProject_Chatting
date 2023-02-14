package Server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SocketServer extends Thread{
	
	
	private Socket socket;
	private List<SocketServer>  socketList = new ArrayList<>();
	private InputStream inputStream;
	private OutputStream outputStream;
	
	public SocketServer(Socket socket) {
		this.socket = socket;
		socketList.add(this);
	}
	
	@Override
	public void run() {
		
	
	}
}
