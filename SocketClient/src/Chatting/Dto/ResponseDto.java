package Chatting.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class ResponseDto<T> {
	private String resource;
	private String username;
	private String status;
	private String createRoomname;
	private String enterRoomname;
	private T body;
}
