package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

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
					
					
					}
					
					
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
