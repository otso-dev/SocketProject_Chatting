package Server.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class RequestDto<T> {
	private String resource;
	private String username;
	private String roomname;
	private T body;
}
