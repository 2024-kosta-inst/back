package com.kosta.domain.request;

import com.kosta.entity.ImageFile;
import com.kosta.entity.Post;
import com.kosta.entity.User;

import lombok.Data;

@Data
public class PostRequest {
	private Long id;
	private String title, content, password;
	private Long authorId;
	private ImageFile imageFile;
	
	public Post toEntity(User author) {
		return Post.builder()
				.title(title)
				.content(content)
				.password(password)
				.author(author)
				.image(imageFile)
				.build();
	}
}