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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import Server.Dto.RequestDto;
import Server.Dto.ResponseDto;
import lombok.Data;

@Data
public class SocketServer extends Thread {

	private static List<SocketServer> socketList = new ArrayList<>();
	private static Map<String, List<SocketServer>> chatRoomMap = new HashMap<>();


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
	            if (!chatRoomMap.containsKey(roomname)) {
	                chatRoomMap.put(roomname, new ArrayList<>());
	            }
	            chatRoomMap.get(roomname).add(this);

	            ResponseDto<?> roomResponseDto = new ResponseDto<List<String>>("createRoom", username, roomname, new ArrayList<String>(chatRoomMap.keySet()));
	            sendToAll(roomResponseDto);
	            break;

	        case "createjoin":
	            String createroomname = (String) requestDto.getBody();
	            if (!chatRoomMap.containsKey(createroomname)) {
	                chatRoomMap.put(createroomname, new ArrayList<>());
	            }
	            System.out.println(chatRoomMap.get(createroomname).add(this)); 

	            ResponseDto<?> joinResponseDto = new ResponseDto<String>("createjoin", username, roomname, createroomname);
	            sendToRoom(joinResponseDto, createroomname);
	            break;

	        case "enter":
	            String chattingRoom = (String) requestDto.getBody();
	            String username = (String) requestDto.getUsername();
	            if (!chatRoomMap.containsKey(chattingRoom)) {
	                chatRoomMap.put(chattingRoom, new ArrayList<>());
	            }
	            System.out.println(chatRoomMap.get(chattingRoom).add(this));
	   

	            ResponseDto<?> chatResponseDto = new ResponseDto<List<String>>("enter", username, chattingRoom, null);
	            sendToRoom(chatResponseDto,chattingRoom);
	            break;

	        case "sendMessage":
	            String message = (String) requestDto.getBody();
	            String chatroom = (String) requestDto.getRoomname();
	            String username1 = (String)requestDto.getUsername();
	            System.out.println(chatroom);
	            ResponseDto<?> messageResponseDto = new ResponseDto<String>("sendMessage", username1, chatroom, message);
	            sendToRoom(messageResponseDto, chatroom);
	            break;
		}
	}
	
	private void sendToAll(ResponseDto<?> responseDto) throws IOException {
	    String response = gson.toJson(responseDto);
	    for (SocketServer socketServer : socketList) {
	    	OutputStream outputStream = socketServer.getSocket().getOutputStream();
	        PrintWriter writer = new PrintWriter(outputStream, true);
	        writer.println(response);
	        writer.flush();
	    }
	}

	private void sendToRoom(ResponseDto<?> responseDto, String roomname) throws IOException {
	    String response = gson.toJson(responseDto);
	    List<SocketServer> socketServers = chatRoomMap.get(roomname);
	    if (socketServers != null) {
	        for (SocketServer socketServer : socketServers) {
	        	outputStream = socketServer.getSocket().getOutputStream();
		        PrintWriter writer = new PrintWriter(outputStream, true);
	            writer.println(response);
	            writer.flush();
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


//	private void sendToAll(ResponseDto<?> responseDto) throws IOException {
//		String response = gson.toJson(responseDto);
//		for (SocketServer socketServer : socketList) {
//
//			outputStream = socketServer.getSocket().getOutputStream();
//			PrintWriter writer = new PrintWriter(outputStream, true);
//			writer.println(response);
//			writer.flush();
//		}
//
//	}

//	private void sendChattRoomCreate(ResponseDto<?> responseDto, String chattingRoom) throws IOException {
//		String response = gson.toJson(responseDto);
//		for (String room : roomList) {
//			if (room.equals(chattingRoom)) {
//				for (SocketServer socketServer : socketList) {
//					if (socketServer.getUsername().equals(username)) {
//						OutputStream outputStream = socketServer.getSocket().getOutputStream();
//						PrintWriter writer = new PrintWriter(outputStream, true);
//
//						writer.println(response);
//						System.out.println(response);
//						writer.flush();
//					}
//
//				}
//			}
//		}
//	}

//	private void sendChatRoom(ResponseDto<?>responseDto) throws IOException {
//		String response = gson.toJson(responseDto);
//		for(String user : chattingUserList) {
//			if(user.equals(username)) {
//				for(SocketServer socketServer : socketList) {
//					OutputStream outputStream = socketServer.getSocket().getOutputStream();
//					PrintWriter writer = new PrintWriter(outputStream, true);
//
//					writer.println(response);
//					System.out.println(response);
//					writer.flush();
//				}
//			}
//		}
//	}


	

