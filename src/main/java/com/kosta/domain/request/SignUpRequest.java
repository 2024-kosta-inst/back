package com.kosta.domain.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SignUpRequest {	
	private String email;
	private String name;
	private String password;
}
