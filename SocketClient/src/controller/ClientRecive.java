package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import com.google.gson.Gson;

import Chatting.ChattingClient;
import Chatting.Dto.RequestDto;
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
				inputStream = socket.getInputStream();
				BufferedReader read = new BufferedReader(new InputStreamReader(inputStream));
				gson = new Gson();
				
				while(true) {
					String request = read.readLine();
					ResponseDto<?> responseDto = gson.fromJson(request, ResponseDto.class);
					switch(responseDto.getResource()) {
						case "join" :
							RequestDto<?> requestDto = gson.fromJson(request, RequestDto.class);
						 	requestDto.getBody();
						 	break;
						 	
						case "roomCreate" :
								RequestDto<?> createRequestDto = gson.fromJson(request, RequestDto.class);
								Controller.getInstance().getChattingClient().getRoomModel().clear(); 
								Controller.getInstance().getChattingClient().getRoomModel().addElement("<채팅방 목록>");
								Controller.getInstance().getChattingClient().getRoomModel().addAll((List<String>) createRequestDto.getBody());
								Controller.getInstance().getChattingClient().getRoomList().setSelectedIndex(0);
								break;
						
						case "sendMessage":
								RequestDto<?> messageRequestDto = gson.fromJson(request, RequestDto.class);
								Controller.getInstance().getChattingClient().getChatArea().append(messageRequestDto.getBody() + "\n");
								break;
								
						case "roomJoin" :
								Controller.getInstance().getChattingClient().getRoomList();
								Controller.getInstance().getChattingClient().getUserId();
								break;
						
					
					}
					
					
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
