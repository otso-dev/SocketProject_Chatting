package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import Server.Dto.RequestDto;
import Server.Dto.ResponseDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class SocketServer extends Thread {

	

	private static List<SocketServer> socketList = new ArrayList<>();
	private static List<String> roomList = new ArrayList<>();
	
	private List<String>chattingUserList;
	
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Gson gson;
	
	private String username;
	private String roomname;

	
	public SocketServer(Socket socket) {
		this.socket = socket;
		socketList.add(this);
		gson = new Gson();
	}

	@Override
	public void run() {
		try {
			reciveRequest();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void reciveRequest() throws IOException {
		inputStream = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		
		while(true) {
			String request = reader.readLine();
			if(request == null) {
				throw new ConnectException();
			}
			RequestMapping(request);
		}
	}
	
	
	private void RequestMapping(String request) throws IOException {
		RequestDto<?> requestDto = gson.fromJson(request, RequestDto.class);
		switch (requestDto.getResource()) {
		case "join":
			username = (String) requestDto.getBody();
			// ResponseDto<?> responseDto = new ResponseDto<String>("join", username);
			// sendResponse(responseDto);
			break;

		case "createRoom":
			roomname = (String) requestDto.getBody();
            if (!roomList.contains(roomname)) {
                roomList.add(roomname);
            }
            ResponseDto<?> responseDto = new ResponseDto<List<String>>("createRoom", roomList);
            sendToAll(responseDto);
            break;
		case "enter":
			String chattingRoom = (String)requestDto.getBody();
			chattingUserList = new ArrayList<>();
			for(String room: roomList) {
				if(room.equals(chattingRoom)) {
					chattingUserList.add(username);
				}
			}
			for(int i = 0; i < chattingUserList.size(); i++) {
				System.out.println(chattingUserList.get(i));
			}
			break;
		}
	}
	
	private void sendToAll(ResponseDto<?> responseDto) throws IOException {
		String response = gson.toJson(responseDto);
		for (SocketServer socketServer : socketList) {
			
			outputStream = socketServer.getSocket().getOutputStream();
			PrintWriter writer = new PrintWriter(outputStream, true);
			writer.println(response);
			writer.flush();
			
			
		}
		
	}

	private void sendResponse(ResponseDto<?> responseDto) throws IOException {
		String response = gson.toJson(responseDto);
		outputStream = socket.getOutputStream();
		PrintWriter writer = new PrintWriter(outputStream, true);
		writer.println(response);
		writer.flush();
	}
}
