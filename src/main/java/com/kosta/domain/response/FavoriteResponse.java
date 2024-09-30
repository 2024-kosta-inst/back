package com.kosta.domain.response;

import com.kosta.domain.FileDTO;
import com.kosta.entity.Favorite;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FavoriteResponse {
	private Long id;
	private String title, url;
	private FileDTO image;
	
	public static FavoriteResponse toDTO(Favorite fav) {
		return FavoriteResponse.builder()
			.id(fav.getId())
			.title(fav.getTitle())
			.url(fav.getUrl())
			.image(FileDTO.toDTO(fav.getImage()))
			.build();
	}
}
