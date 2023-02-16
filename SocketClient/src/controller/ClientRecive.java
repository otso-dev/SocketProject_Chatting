package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import com.google.gson.Gson;

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
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
