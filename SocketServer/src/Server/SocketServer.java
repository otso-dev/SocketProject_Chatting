package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import Server.Dto.RequestDto;
import Server.Dto.ResponseDto;
import lombok.Data;

@Data
public class SocketServer extends Thread{
	
	
	private Socket socket;
	private List<SocketServer>  socketList = new ArrayList<>();
	private InputStream inputStream;
	private OutputStream outputStream;
	private Gson gson;
	private String userId;
	private String room;
	
	private static List<String> roomName = new ArrayList<>();
	private static Map<String, List<SocketServer>> chattingRoom = new HashMap<>();
	
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
			break;
		case "roomCreate":
			room = (String) requestDto.getBody();
			ResponseDto<?> roomresponDto = new ResponseDto<List<String>>("makeRoom", roomName, room, null);
			if(!chattingRoom.containsKey(room)) {
				chattingRoom.put(room, new ArrayList<>());
			}
			chattingRoom.get(room).add(this);
						
			
		}
	
	}
	private void sendResponse(ResponseDto<?> responseDto) {
		
	}
}
