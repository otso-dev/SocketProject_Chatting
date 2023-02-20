package Chatting.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.Gson;

import Chatting.Dto.ResponseDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientRecive extends Thread{

	private final Socket socket;
	private InputStream inputStream;
	private Gson gson;
	
	
	@Override
	public void run() {
		try {
			gson = new Gson();
			while(true) {
				reciveRequest();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void reciveRequest() throws IOException {
		inputStream = socket.getInputStream();
		BufferedReader read = new BufferedReader(new InputStreamReader(inputStream));
		String request = read.readLine();
		ResponseDto<?> responseDto = gson.fromJson(request,ResponseDto.class);
		switch (responseDto.getResource()) {
		case "createRoom":
			System.out.println(responseDto);
		

			Controller.getInstance().getChattingClient().getRoomListModel().clear();
			Controller.getInstance().getChattingClient().getRoomListModel().addElement("================<<방목록>>================");
			Controller.getInstance().getChattingClient().getRoomListModel().addAll((List<String>)responseDto.getBody());

			Controller.getInstance().getChattingClient().getRoomList().setSelectedIndex(0);
			break;
			
		case "createjoin":
			Controller.getInstance().getChattingClient().getChatArea().setText("");
			Controller.getInstance().getChattingClient().getChatArea().append("***" + responseDto.getBody() + "***" + "방을 생성하였습니다.\n");
			break;
			
		case "enter":
			Controller.getInstance().getChattingClient().getChatArea().append("[" + responseDto.getUsername()+ "]" + "님이 접속하였습니다.\n");
			break;
		case "sendMessage":
			System.out.println("sendMessage: " + responseDto);
			Controller.getInstance().getChattingClient().getChatArea().append("[" + responseDto.getUsername()+ "]" + ": " + responseDto.getBody() + "\n");
			break;
		case "leave":
			Controller.getInstance().getChattingClient().getChatArea().append(responseDto.getUsername() + "님이 " + "["+ responseDto.getEnterRoomname()+"]" + " 방에서 나갔습니다.\n");
			break;
		default:
			System.out.println("해당 요청은 처리 할 수 없습니다.");
			break;
		}
		
	}
}
