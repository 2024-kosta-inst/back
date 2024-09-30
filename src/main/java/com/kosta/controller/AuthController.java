package com.kosta.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.domain.request.SignUpRequest;
import com.kosta.domain.request.UserDeleteRequest;
import com.kosta.domain.request.UserUpdateRequest;
import com.kosta.domain.response.LoginResponse;
import com.kosta.domain.response.UserResponse;
import com.kosta.service.AuthService;
import com.kosta.util.TokenUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	private final TokenUtils tokenUtils;
	
	// 토큰 재발급
	@PostMapping("/refresh-token")
	public ResponseEntity<LoginResponse> refreshToken(HttpServletRequest req, HttpServletResponse res) {
		// 토큰 요청
		Map<String, String> tokenMap = authService.refreshToken(req);

		// 토큰 재발급 불가인 경우, 401 에러 반환
		if (tokenMap == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		
		// 헤더 Cookie로 refresh 토큰 재발급
		tokenUtils.setRefreshTokenCookie(res, tokenMap.get("refreshToken"));
		
		// 응답 Body로 access 토큰 재발급
		return ResponseEntity.ok(
				LoginResponse.builder()
				.accessToken(tokenMap.get("accessToken"))
				.build());
	}
	
	// 회원가입
	@PostMapping("/signup")
	public ResponseEntity<UserResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
		log.info("[signUp] 회원가입 진행. 요청정보 : {}", signUpRequest);
		UserResponse userResponse = authService.signUp(signUpRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
	}
	
	// 이메일 중복확인
	@GetMapping("/duplicate")
	public ResponseEntity<Boolean> emailCheck(@RequestParam("email") String email) {
		boolean isNotDuplicate = authService.duplicateCheckEmail(email);
		return ResponseEntity.ok(isNotDuplicate);
	}
	
	// 회원 전체 리스트
	@GetMapping("")
	public ResponseEntity<List<UserResponse>> getUserList() {
		log.info("[getUserList] 회원 전체 조회");
		List<UserResponse> userList = authService.getUserList();
		return ResponseEntity.ok(userList);
	}
	
	// 회원 정보 수정
	@PatchMapping("")
	public ResponseEntity<UserResponse> updateUser(@RequestBody UserUpdateRequest userUpdateReqeust) {
		log.info("[updateUser] 회원 정보 수정. 수정 요청 정보 : {}", userUpdateReqeust);
		UserResponse userResponse = authService.updateUser(userUpdateReqeust);
		return ResponseEntity.ok(userResponse);
	}
	
	// 회원 삭제
	@DeleteMapping("")
	public ResponseEntity<?> userWithdrawal(@RequestBody UserDeleteRequest userDeleteRequest) {
		log.info("[updateUser] 회원 삭제. 삭제 요청 정보 : {}", userDeleteRequest);
		authService.deleteUser(userDeleteRequest);
		return ResponseEntity.ok(null);
	}	
}
