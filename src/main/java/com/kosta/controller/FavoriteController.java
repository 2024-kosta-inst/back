package com.kosta.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.domain.request.FavoriteRequest;
import com.kosta.domain.response.ErrorResponse;
import com.kosta.domain.response.FavoriteResponse;
import com.kosta.service.FavoriteService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite")
public class FavoriteController {
	private final FavoriteService favoriteService;
	
	// application.yml 파일의 location 정보 가져오기
	@Value("${spring.upload.location}")
	private String uploadPath;
	
	// 추가
	@PostMapping("")
	public ResponseEntity<FavoriteResponse> writeFav(FavoriteRequest fav, @RequestParam(name="image", required = false) MultipartFile file) {
		FavoriteResponse savedFav = favoriteService.insertFav(fav, file);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedFav);
	}
	
	// 전체 링크 리스트 조회
	@GetMapping("")
	public ResponseEntity<List<FavoriteResponse>> getAllFav() {
		List<FavoriteResponse> result = favoriteService.getAllFav();
		return ResponseEntity.ok(result);
	}
	
	// 링크 수정
	@PatchMapping("")
	public ResponseEntity<FavoriteResponse> modifyFav(FavoriteRequest fav, @RequestParam(name="image", required = false) MultipartFile file) {
		FavoriteResponse updatedFav = favoriteService.updateFav(fav, file);
		return ResponseEntity.ok(updatedFav);
	}
	
	// 삭제
	@DeleteMapping("/{id}")
	public ResponseEntity<FavoriteResponse> removePost(@PathVariable("id") Long id) {
		FavoriteResponse deletedFav= favoriteService.deleteFav(id);
		return ResponseEntity.ok(deletedFav);
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handlerPostException(RuntimeException e, HttpServletRequest req) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(
					ErrorResponse.builder()
						.statusCode(HttpStatus.BAD_REQUEST.value())
						.message("링크 관련 에러 발생")
						.url(req.getRequestURI())
						.details(e.getMessage())
						.build()
				);
	}
}
