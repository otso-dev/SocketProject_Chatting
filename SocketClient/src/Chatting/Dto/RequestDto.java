package Chatting.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class RequestDto<T> {
	private String resource;
	private String roomname;
	private String username;
	private T body;
}
