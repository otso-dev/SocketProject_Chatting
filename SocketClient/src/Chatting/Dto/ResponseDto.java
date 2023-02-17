package Chatting.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
public class ResponseDto<T> {
	private String resource;
	private String username;
	private String roomname;
	private T body;
}
