package Chatting.controller;

import java.awt.Container;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

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
		System.out.println(responseDto);
		switch (responseDto.getResource()) {
		case "join":
			System.out.println(responseDto);
			Controller.getInstance().getChattingClient().getLabelUsername().setText((String) responseDto.getUsername());
			Controller.getInstance().getChattingClient().getRoomListModel().clear();
			Controller.getInstance().getChattingClient().getRoomListModel().addElement("============================<<방목록>>============================");
			Controller.getInstance().getChattingClient().getRoomListModel().addAll((List<String>)responseDto.getBody());
			Controller.getInstance().getChattingClient().getRoomList().setSelectedIndex(0);
			Controller.getInstance().getChattingClient().getMainCard().show(Controller.getInstance().getChattingClient().getMainPanel(), "RoomPanel");
			break;
		case "createRoom":
			System.out.println(responseDto);
			
			Controller.getInstance().getChattingClient().getRoomListModel().clear();
			Controller.getInstance().getChattingClient().getRoomListModel().addElement("============================<<방목록>>============================");
			Controller.getInstance().getChattingClient().getRoomListModel().addAll((List<String>)responseDto.getBody());
			Controller.getInstance().getChattingClient().getRoomList().setSelectedIndex(0);
			if(responseDto.getStatus().equalsIgnoreCase("ok")) {
				RequestDto<?> reqCreatejoin = RequestDto.<String>builder().resource("createjoin")
						.username(Controller.getInstance().getChattingClient().getUsername())
						.body(Controller.getInstance().getChattingClient().getCreateRoom())
						.build();
				sendRequest(reqCreatejoin);
			}
			break;
		case"createjoin":
				Controller.getInstance().getChattingClient().getChatArea().setText("");
				System.out.println(responseDto.getBody());
				Controller.getInstance().getChattingClient().getLabelChatUsername().setText(Controller.getInstance().getChattingClient().getUsername());
				Controller.getInstance().getChattingClient().getMainCard().show(Controller.getInstance().getChattingClient().getMainPanel(),"ChattingPanel");
				Controller.getInstance().getChattingClient().getChattingRoomName().setText("제목: " + (String)responseDto.getBody()+"의 방");
				Controller.getInstance().getChattingClient().getChatArea().append("***" + responseDto.getBody() + "***" + "방을 생성하였습니다.\n");
			break;
		case "enter":
			Controller.getInstance().getChattingClient().getLabelChatUsername().setText(Controller.getInstance().getChattingClient().getUsername());
			Controller.getInstance().getChattingClient().getChatArea().append("[" + responseDto.getUsername()+ "]" + "님이 접속하였습니다.\n");
			break;
		case "sendMessage":
			System.out.println("sendMessage: " + responseDto);
			Controller.getInstance().getChattingClient().getChatArea().append("[" + responseDto.getUsername()+ "]" + ": " + responseDto.getBody() + "\n");
			break;
		case "leave":
			Controller.getInstance().getChattingClient().getChatArea().append(responseDto.getUsername() + "님이 " + "["+ responseDto.getEnterRoomname()+"]" + " 방에서 나갔습니다.\n");
			break;
		case "AllLeave":
			System.out.println(responseDto);
			Controller.getInstance().getChattingClient().getMainCard().show(Controller.getInstance().getChattingClient().getMainPanel(),"RoomPanel");
			Controller.getInstance().getChattingClient().setCreateRoom(null);
			JOptionPane.showMessageDialog(null, "[" + responseDto.getUsername() + "]" +" 방장이 나갔습니다.","out",JOptionPane.YES_OPTION);
			break;
			
		case "removeRoom":
			Controller.getInstance().getChattingClient().getRoomListModel().clear();
			Controller.getInstance().getChattingClient().getRoomListModel().addElement("============================<<방목록>>============================");
			Controller.getInstance().getChattingClient().getRoomListModel().addAll((List<String>)responseDto.getBody());
			Controller.getInstance().getChattingClient().getRoomList().setSelectedIndex(0);
			break;
			
		case "error":
			reciveError(responseDto);
			break;
		default:
			System.out.println("해당 요청은 처리 할 수 없습니다.");
			break;
		}
		
	}
	
	private void sendRequest(RequestDto<?> requestDto) {
		try {
			OutputStream outputStream = socket.getOutputStream();
			PrintWriter out = new PrintWriter(outputStream, true);

			out.println(gson.toJson(requestDto));
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private void reciveError(ResponseDto<?> responseDto) {
		switch (responseDto.getStatus()) {
		case "join":
			JOptionPane.showMessageDialog(null, responseDto.getBody(),"error",JOptionPane.CLOSED_OPTION);
			Controller.getInstance().getChattingClient().setJoinflag(false);
			break;
			
		case "createRoom":
			JOptionPane.showMessageDialog(null, responseDto.getBody(),"error",JOptionPane.CLOSED_OPTION);
			break;
		default:
			break;
		}
	}
}
