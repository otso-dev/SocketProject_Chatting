package Server.Dto;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class RequestDto<T> {
	private String resource;
	private String username;
	private String createRoomname;
	private String enterRoomname;
	private T body;
}
