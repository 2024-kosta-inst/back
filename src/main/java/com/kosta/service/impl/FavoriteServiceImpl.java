package com.kosta.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.domain.request.FavoriteRequest;
import com.kosta.domain.response.FavoriteResponse;
import com.kosta.entity.Favorite;
import com.kosta.entity.ImageFile;
import com.kosta.repository.FavoriteRepository;
import com.kosta.service.FavoriteService;
import com.kosta.service.ImageFileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
	private final FavoriteRepository favoriteRepository;
	private final ImageFileService imageFileService;
	
	@Override
	public FavoriteResponse insertFav(FavoriteRequest fav, MultipartFile file) {
		ImageFile savedImage = imageFileService.saveImage(file);
		if (savedImage != null) fav.setImageFile(savedImage);
		Favorite savedFav = favoriteRepository.save(fav.toEntity());
		return FavoriteResponse.toDTO(savedFav);
	}
	
	@Override
	public List<FavoriteResponse> getAllFav() {
		return favoriteRepository.findAll().stream().map(f -> FavoriteResponse.toDTO(f)).toList();
	}
	
	@Override
	public FavoriteResponse updateFav(FavoriteRequest favDTO, MultipartFile file) {
		
		Long favId = favDTO.getId();
		Favorite fav = favoriteRepository.findById(favId).orElseThrow(() -> new IllegalArgumentException("없는 링크"));
		
		ImageFile savedImage = imageFileService.saveImage(file);
		if (savedImage != null) fav.setImage(savedImage);
		if (favDTO.getTitle() != null) fav.setTitle(favDTO.getTitle());
		if (favDTO.getUrl() != null) fav.setUrl(favDTO.getUrl());
		
		if (favDTO.isDeleteImage()) fav.setImage(null);
		
		Favorite updatedFav = favoriteRepository.save(fav);
		return FavoriteResponse.toDTO(updatedFav);
	}
	
	@Override
	public FavoriteResponse deleteFav(Long favId) {
		Favorite fav = favoriteRepository.findById(favId).orElseThrow(() -> new IllegalArgumentException("없는 링크"));
		favoriteRepository.deleteById(fav.getId());
		return FavoriteResponse.toDTO(fav);
	}

	
}
