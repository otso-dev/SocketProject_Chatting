package Chatting.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void reciveRequest() throws IOException {
		inputStream = socket.getInputStream();
		BufferedReader read = new BufferedReader(new InputStreamReader(inputStream));
		String request = read.readLine();
		System.out.println(request);
		ResponseDto<?> responseDto = gson.fromJson(request,ResponseDto.class);
		System.out.println(responseDto);
		switch (responseDto.getResource()) {
		case "createRoom":
			Controller.getInstance().getChattingClient().getRoomListModel().clear();
			Controller.getInstance().getChattingClient().getRoomListModel().addAll((List<String>) responseDto.getBody());
			System.out.println("CR: " + Controller.getInstance().getChattingClient().getRoomListModel().hashCode()); 			
			Controller.getInstance().getChattingClient().getRoomList().setSelectedIndex(0);
			break;

		default:
			break;
		}
		
	}
}
