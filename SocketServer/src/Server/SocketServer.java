package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import Server.Dto.RequestDto;
import Server.Dto.ResponseDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class SocketServer extends Thread{
	
	
	private Socket socket;
	
	private List<SocketServer>  socketList = new ArrayList<>();
	
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
			RequestDto<?> requestDto = gson.fromJson(request,RequestDto.class);
			//System.out.println(requestDto+"ServerreciveReq");
			switch (requestDto.getResource()) {
			case "join":
				String username = (String) requestDto.getBody();
				System.out.println(username);
				break;

			case"createRoom":
				roomname = (String) requestDto.getBody();
				System.out.println(roomname);
				List<String> roomList = new ArrayList<>();
				for(SocketServer socketServer : socketList) {
					roomList.add(socketServer.getRoomname());
				}
				ResponseDto<?> responseDto = new ResponseDto<List<String>>("createRoom", roomList);
				//System.out.println(responseDto);
				sendToAll(responseDto);
				break;
			}
		}
		
		
	}
	
	private void sendToAll(ResponseDto<?> responseDto) throws IOException {
		String response = gson.toJson(responseDto);
		for(SocketServer socketServer : socketList) {
			OutputStream outputStream = socketServer.getOutputStream();
			PrintWriter writer = new PrintWriter(outputStream);
			
			writer.print(response);
		}
	}
	private void sendResponse(ResponseDto<?>responseDto) throws IOException{
		String response = gson.toJson(responseDto);
		OutputStream outputStream = socket.getOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);
		writer.print(response);
	}
}
