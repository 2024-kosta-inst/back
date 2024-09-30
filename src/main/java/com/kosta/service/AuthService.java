package com.kosta.service;

import java.util.List;
import java.util.Map;

import com.kosta.domain.request.SignUpRequest;
import com.kosta.domain.request.UserDeleteRequest;
import com.kosta.domain.request.UserUpdateRequest;
import com.kosta.domain.response.UserResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

	UserResponse signUp(SignUpRequest signUpRequest);

	List<UserResponse> getUserList();

	UserResponse updateUser(UserUpdateRequest userUpdateReqeust);

	void deleteUser(UserDeleteRequest userDeleteRequest);

	boolean duplicateCheckEmail(String email);

	Map<String, String> refreshToken(HttpServletRequest req);
}
