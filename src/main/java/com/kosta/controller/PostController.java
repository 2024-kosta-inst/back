package com.kosta.controller;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import com.kosta.domain.FileDTO;
import com.kosta.domain.request.PostRequest;
import com.kosta.domain.response.ErrorResponse;
import com.kosta.domain.response.PostResponse;
import com.kosta.service.ImageFileService;
import com.kosta.service.PostService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController // 모든 메소드에 @ResponseBody를 적용
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
	private final PostService postService;
	private final ImageFileService imageFileService;
	
	// application.yml 파일의 location 정보 가져오기
	@Value("${spring.upload.location}")
	private String uploadPath;
	
	// 추가
	@PostMapping("")
	/* formData */
	/* { "title" : "제목", "content" : "내용", "password" : "1234", "authorId" : 1 } */
	public ResponseEntity<PostResponse> writePost(PostRequest post, @RequestParam(name="image", required = false) MultipartFile file) {
		PostResponse savedPost = postService.insertPost(post, file);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
	}
	
	// 전체 게시물 조회
	// localhost:8080/api/post
	@GetMapping("")
	public ResponseEntity<List<PostResponse>> getAllPost(@RequestParam(name = "id", required = false) Long id) {
		List<PostResponse> result = new ArrayList<>();
		if (id == null) {
			result = postService.getAllPost();
		} else {
			PostResponse postResponse = postService.getPostById(id);
			result.add(postResponse);
		}
		// return ResponseEntity.status(HttpStatus.OK).body(result);
		return ResponseEntity.ok(result);
	}
	
	// id를 통한 특정 게시물 조회
	@GetMapping("/{id}")
	public ResponseEntity<PostResponse> getPost(@PathVariable("id") Long id) {
		PostResponse postResponse = postService.getPostById(id);
		return ResponseEntity.ok(postResponse);
	}
	
	// 수정
	@PatchMapping("")
	/* { "id" : 1, "title" : "제목 수정", "content" : "내용 수정", "password" : "1234", "authorId" : 1 } */
	public ResponseEntity<PostResponse> modifyPost(PostRequest post, @RequestParam(name="image", required = false) MultipartFile file) {
		PostResponse updatedPost = postService.updatePost(post, file);
		return ResponseEntity.ok(updatedPost);
	}
	
	// 삭제
	@DeleteMapping("/{id}")
	/* { "password" : "1234", "authorId" : 1 } */
	public ResponseEntity<PostResponse> removePost(@PathVariable("id") Long id, @RequestBody PostRequest post) {
		PostResponse deletedPost = postService.deletePost(id, post);
		return ResponseEntity.ok(deletedPost);
	}
	
	// 파일 다운로드
	@GetMapping("/download/{imageId}")
	public ResponseEntity<Resource> downloadImage(@PathVariable("imageId") Long id) throws MalformedURLException {
		FileDTO fileDTO = imageFileService.getImageByImageId(id);
		
		UrlResource resource = new UrlResource("file:" + uploadPath + "\\" + fileDTO.getSaved());
		String fileName = UriUtils.encode(fileDTO.getOrigin(), StandardCharsets.UTF_8);
		String contentDisposition = "attachment; filename=\"" + fileName + "\"";
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
				.body(resource);
	}
	
	// 검색
	@GetMapping("/search")
	public ResponseEntity<List<PostResponse>> search(@RequestParam("keyword") String keyword) {
		List<PostResponse> result = postService.search(keyword);
		return ResponseEntity.ok(result);
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handlerPostException(RuntimeException e, HttpServletRequest req) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(
					ErrorResponse.builder()
						.statusCode(HttpStatus.BAD_REQUEST.value())
						.message("게시물 관련 에러 발생")
						.url(req.getRequestURI())
						.details(e.getMessage())
						.build()
				);
	}
	
//	@GetMapping("/news/{id}")
//	public ResponseEntity<String> getNewsById(@PathVariable("id") int id) {
//		// RestTemplate 인스턴스를 하나 생성
//		RestTemplate rt = new RestTemplate();
//		
//		// 헤더 인스턴스 생성
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json; charset=utf-8");
//		HttpEntity<String> httpEntity = new HttpEntity<String>(headers); 
//				
//		// URL 주소 만들기
//		String url = "https://jsonplaceholder.typicode.com/posts/" + id;
//		
//		try {
////			ResponseEntity<String> result = rt.getForEntity(url, String.class);
//			ResponseEntity<String> result = rt.exchange(url, HttpMethod.GET, httpEntity, String.class);
//			return result;			
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).body("에러!");
//		}
//	}
}
