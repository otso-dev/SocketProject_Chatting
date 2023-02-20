package Server.Dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResponseDto<T> {
	private String resource;
	private T body;
	private String room;
	private String userId;
	private String roomName;
}
