package com.kosta.domain.request;

import com.kosta.entity.Favorite;
import com.kosta.entity.ImageFile;

import lombok.Data;

@Data
public class FavoriteRequest {
	private Long id;
	private String title, url;	
	private ImageFile imageFile;
	private boolean deleteImage;
	
	public Favorite toEntity() {
		return Favorite.builder()
				.title(title)
				.url(url)
				.image(imageFile)
				.build();
	}
}