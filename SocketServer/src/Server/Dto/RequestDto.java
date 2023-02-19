package Server.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RequestDto<T> {
	private String resource;
	private T body;
	private String room;
	private String userId;
}
