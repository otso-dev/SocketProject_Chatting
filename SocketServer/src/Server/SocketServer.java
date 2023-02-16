package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import Server.Dto.RequestDto;
import lombok.Data;

@Data
public class SocketServer extends Thread{
	
	
	private Socket socket;
	private List<SocketServer>  socketList = new ArrayList<>();
	private InputStream inputStream;
	private OutputStream outputStream;
	private Gson gson;
	private String userId;
	
	public SocketServer(Socket socket) {
		this.socket = socket;
		socketList.add(this);
		gson = new Gson();
	}
	
	@Override
	public void run() {
		try {
			reciveRequest();;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void reciveRequest() throws IOException {
		inputStream = socket.getInputStream();
		BufferedReader read = new BufferedReader(new InputStreamReader(inputStream));
		
		while(true) {
			String request = read.readLine();
			if(request == null) {
				throw new ConnectException();
			}
			
			RequestMapping(request);
		}
	}
	private void RequestMapping(String request) throws IOException {
		RequestDto<?> requestDto = gson.fromJson(request, RequestDto.class);
		String resource = requestDto.getResource() ;
		switch(resource) {
		case "join" :
			userId = (String) requestDto.getBody();
						
			
		}
	
	}
}
