package com.kosta.service;

import jakarta.servlet.http.HttpServletResponse;

public interface OAuthService {

	String oAuthSignIn(String code, String provider, HttpServletResponse res);

}
