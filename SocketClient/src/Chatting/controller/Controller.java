package Chatting.controller;

import Chatting.ChattingClient;
import lombok.Data;

@Data
public class Controller {
	private static Controller instance;
	private ChattingClient chattingClient;
	
	private Controller() {
		this.chattingClient = new ChattingClient();
	}
	
	public static Controller getInstance() {
		if(instance == null) {
			instance = new Controller();
		}
		return instance;
	}
}
