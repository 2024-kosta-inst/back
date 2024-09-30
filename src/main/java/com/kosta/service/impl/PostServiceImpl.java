package com.kosta.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.domain.request.PostRequest;
import com.kosta.domain.response.PostResponse;
import com.kosta.entity.ImageFile;
import com.kosta.entity.Post;
import com.kosta.entity.User;
import com.kosta.repository.PostRepository;
import com.kosta.repository.UserRepository;
import com.kosta.service.ImageFileService;
import com.kosta.service.PostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final ImageFileService imageFileService;
	
	@Override
	public PostResponse insertPost(PostRequest postDTO, MultipartFile file) {
		
		ImageFile savedImage = imageFileService.saveImage(file);
		if (savedImage != null) {
			// postDTO 객체에 image 객체 넣기
			postDTO.setImageFile(savedImage);
		}
		
		User user = userRepository.findById(postDTO.getAuthorId())
			.orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없음"));
		Post post = postDTO.toEntity(user);
		Post savedPost = postRepository.save(post);
	
		PostResponse result = PostResponse.toDTO(savedPost);
		
		return result;
	}

	@Override
	public List<PostResponse> getAllPost() {
		List<Post> postList = postRepository.findAll();
		if (postList.size() > 0) {
			// List<PostResponse> postResponseList = postList.stream().map(post -> PostResponse.toDTO(post)).toList();
			List<PostResponse> postResponseList = postList.stream().map(PostResponse::toDTO).toList();
			return postResponseList;			
		} else {
			return new ArrayList<>();
		}
	}

	@Override
	public PostResponse getPostById(Long id) {
		Post post = postRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없음"));
		PostResponse postResponse = PostResponse.toDTO(post);
		return postResponse;
	}

	@Override
	public PostResponse updatePost(PostRequest postDTO, MultipartFile file) {
		User user = userRepository.findById(postDTO.getAuthorId())
				.orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없음"));
		
		Post post = postRepository.findById(postDTO.getId())
				.orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없음"));
		
		if (!post.getAuthor().getId().equals(user.getId())) {
			throw new IllegalArgumentException("본인이 작성한 글만 수정할 수 있음");
		}
		
		if (!post.getPassword().equals(postDTO.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않음");
		}
		
		ImageFile savedImage = imageFileService.saveImage(file);
		if(savedImage != null) post.setImage(savedImage);
		if(postDTO.getTitle() != null) post.setTitle(postDTO.getTitle());
		if(postDTO.getContent() != null) post.setContent(postDTO.getContent());
		
		Post updatedPost = postRepository.save(post);
		PostResponse result = PostResponse.toDTO(updatedPost);
		return result;
	}

	@Override
	public PostResponse deletePost(Long id, PostRequest postDTO) {
		User user = userRepository.findById(postDTO.getAuthorId())
				.orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없음"));
		
		Post post = postRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("해당 게시물을 찾을 수 없음"));
		
		if (!post.getAuthor().getId().equals(user.getId())) {
			throw new IllegalArgumentException("본인이 작성한 글만 삭제할 수 있음");
		}
		
		if (!post.getPassword().equals(postDTO.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않음");
		}
		
		postRepository.delete(post);
		return PostResponse.toDTO(post);
	}

	@Override
	public List<PostResponse> search(String keyword) {
		List<Post> postList = postRepository.findByTitleContainsOrContentContains(keyword, keyword);
		return postList.stream().map(p -> PostResponse.toDTO(p)).toList();
	}
}
