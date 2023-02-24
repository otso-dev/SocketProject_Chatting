package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;

import Server.Dto.RequestDto;
import Server.Dto.ResponseDto;
import lombok.Data;

@Data
public class SocketServer extends Thread {

	private static List<SocketServer> socketList = new ArrayList<>();
	private static Map<String, List<SocketServer>> chatRoomMap = new LinkedHashMap<>();
	private static List<String> userList = new ArrayList<>();
	
	
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Gson gson;

	private String username;
	private String createRoomName;
	private String enterRoomName;

	public SocketServer(Socket socket) {
		this.socket = socket;
		socketList.add(this);
		gson = new Gson();
	}

	@Override
	public void run() {
		try {
			reciveRequest();
			System.out.println(socket);
		} catch (SocketException e) {
			if(socket != null) {
				socketList.remove(this);
				System.out.println("채팅 프로그램을 종료하였습니다.");
			}
			
		} catch (IOException e) {
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
			
			String joinUserName = (String) requestDto.getBody();
			for (SocketServer users : socketList) {
				if(users.getUsername() == null) {
					username = (String) requestDto.getBody();
					ResponseDto<?> joinrespDto = ResponseDto.<List<String>>builder().resource("join").username(username)
							.body(new ArrayList<String>(chatRoomMap.keySet())).build();
					userList.add(users.getUsername());
					sendResponse(joinrespDto);
				}
				else if (users.getUsername().equals(joinUserName)) {
					ResponseDto<?> joinErrorRespDto = ResponseDto.<String>builder().resource("error").status("join")
							.body("해당 이름은 이미 존재합니다.").build();
					sendResponse(joinErrorRespDto);
					break;
				}
			}
				
			break;
		case "createRoom":
			createRoomName = (String) requestDto.getBody();
			if (!chatRoomMap.containsKey(createRoomName)) {
				chatRoomMap.put(createRoomName, new ArrayList<>());
				chatRoomMap.get(createRoomName).add(this);
				ResponseDto<?> roomResponseDto = ResponseDto.<List<String>>builder().resource("createRoom")
						.username(username).status("ok").createRoomname(createRoomName)
						.body(new ArrayList<String>(chatRoomMap.keySet())).build();
				sendToAll(roomResponseDto);

			} else {
				ResponseDto<?> createError = ResponseDto.<String>builder().resource("error").status("createRoom")
						.body("해당 방이름은 이미 존재합니다.").build();
				sendResponse(createError);
			}

			break;
		case "createjoin":
			String createroomname = (String) requestDto.getBody();
			String user = (String) requestDto.getUsername();
			ResponseDto<?> joinResponseDto = ResponseDto.<String>builder().resource("createjoin").username(user)
					.body(createroomname).build();
			sendToRoom(joinResponseDto, createroomname);
			break;

		case "enter":
			String enterRoom = (String) requestDto.getBody();
			String enterUsername = (String) requestDto.getUsername();
			chatRoomMap.get(enterRoom).add(this);
			ResponseDto<?> chatResponseDto = ResponseDto.<List<String>>builder().resource("enter")
					.username(enterUsername).enterRoomname(enterRoom).body(null).build();
			sendToRoom(chatResponseDto, enterRoom);
			break;
		case "leave":
			String leaveRoomname = (String) requestDto.getBody();
			moveRoom(leaveRoomname);
			break;

		case "sendMessage":
			String message = (String) requestDto.getBody();
			enterRoomName = (String) requestDto.getEnterRoomname();
			String username1 = (String) requestDto.getUsername();

			ResponseDto<?> messageResponseDto = ResponseDto.<String>builder().resource("sendMessage")
					.username(username1).enterRoomname(enterRoomName).body(message).build();
			sendToRoom(messageResponseDto, enterRoomName);
			break;
		case "AllLeave":
			String Kinguser = (String) requestDto.getUsername();
			createRoomName = (String) requestDto.getBody();
			moveAll(Kinguser, createRoomName);
			break;

		case "removeRoom":
			String deleteRoom = (String) requestDto.getBody();
			removeRoom(deleteRoom);
			break;
		case "userlist":
			
			
			break;
		}
	}

	private void sendResponse(ResponseDto<?> responseDto) throws IOException {
		String response = gson.toJson(responseDto);
		OutputStream outputStream = socket.getOutputStream();
		PrintWriter writer = new PrintWriter(outputStream, true);
		writer.println(response);
		writer.flush();
	}

	private void sendToAll(ResponseDto<?> responseDto) throws IOException {
		for (SocketServer socketServer : socketList) {
			socketServer.sendResponse(responseDto);
		}
	}

	private void sendToRoom(ResponseDto<?> responseDto, String roomname) throws IOException {
		List<SocketServer> socketServers = chatRoomMap.get(roomname);
		if (socketServers != null) {
			for (SocketServer socketServer : socketServers) {
				socketServer.sendResponse(responseDto);
			}
		}
	}

	private void moveRoom(String enterRoomName) throws IOException {
		// 현재 방에서 나가기
		List<SocketServer> currentRoom = chatRoomMap.get(enterRoomName);
		currentRoom.remove(this);
		ResponseDto<?> leaveResponseDto = ResponseDto.<String>builder().resource("leave").username(username)
				.enterRoomname(enterRoomName).body(null).build();
		sendToRoom(leaveResponseDto, enterRoomName);

	}

	private void moveAll(String Kinguser, String roomname) throws IOException {
		List<SocketServer> moveAllRoom = chatRoomMap.get(roomname);
		ResponseDto<?> moveAllResponseDto = ResponseDto.<String>builder().resource("AllLeave").username(Kinguser)
				.body(roomname).build();

		sendToRoom(moveAllResponseDto, roomname);
		if (!moveAllRoom.isEmpty()) {
			moveAllRoom.removeAll(moveAllRoom);
		}

	}

	private void removeRoom(String roomname) throws IOException {
		if (chatRoomMap.containsKey(roomname)) {
			chatRoomMap.remove(roomname);
			ResponseDto<?> removeRoomResponseDto = ResponseDto.<List<String>>builder().resource("removeRoom")
					.body(new ArrayList<String>(chatRoomMap.keySet())).build();
			sendToAll(removeRoomResponseDto);
		}

	}

}
