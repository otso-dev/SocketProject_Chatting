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

	private List<String> chattingUserList;

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

		while (true) {
			String request = reader.readLine();
			if (request == null) {
				throw new ConnectException();
			}
			RequestMapping(request);
		}
	}

	private void RequestMapping(String request) throws IOException {
		RequestDto<?> requestDto = gson.fromJson(request, RequestDto.class);
		System.out.println(requestDto);
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

			ResponseDto<?> roomResponseDto = new ResponseDto<List<String>>("createRoom", null, roomname, roomList);
			sendToAll(roomResponseDto);
			break;

		case "createjoin":
			String createroomname = (String) requestDto.getBody();
			ResponseDto<?> joinResponseDto = new ResponseDto<String>("createjoin", null, null, createroomname);
			sendChattRoomCreate(joinResponseDto, createroomname);
			break;
		case "enter":
			String chattingRoom = (String) requestDto.getBody();
			String username = (String) requestDto.getUsername();
			System.out.println(username);
			System.out.println(chattingRoom);
			chattingUserList = new ArrayList<>();
			for (String room : roomList) {
				if (room.equals(chattingRoom)) {
					chattingUserList.add(username);
				}
			}
			ResponseDto<?> chatResponseDto = new ResponseDto<List<String>>("enter", username, chattingRoom,
					chattingUserList);
			sendChatRoom(chatResponseDto);
			break;

		case "sendmessage":
			String message = (String) requestDto.getBody();

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

	private void sendChattRoomCreate(ResponseDto<?> responseDto, String chattingRoom) throws IOException {
		String response = gson.toJson(responseDto);
		for (String room : roomList) {
			if (room.equals(chattingRoom)) {
				for (SocketServer socketServer : socketList) {
					if (socketServer.getUsername().equals(username)) {
						OutputStream outputStream = socketServer.getSocket().getOutputStream();
						PrintWriter writer = new PrintWriter(outputStream, true);

						writer.println(response);
						System.out.println(response);
						writer.flush();
					}

				}
			}
		}
	}

	private void sendChatRoom(ResponseDto<?>responseDto) throws IOException {
		String response = gson.toJson(responseDto);
		for(String name : chattingUserList) {
			if(name != null) {
				for(SocketServer socketServer : socketList) {
					OutputStream outputStream = socketServer.getSocket().getOutputStream();
					PrintWriter writer = new PrintWriter(outputStream,true);
					
					writer.println(response);
					writer.flush();
				}
			}
			
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
