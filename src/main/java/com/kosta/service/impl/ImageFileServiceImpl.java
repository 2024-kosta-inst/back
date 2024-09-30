package com.kosta.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.domain.FileDTO;
import com.kosta.entity.ImageFile;
import com.kosta.repository.ImageFileRepository;
import com.kosta.service.ImageFileService;
import com.kosta.util.FileUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageFileServiceImpl implements ImageFileService {
	private final ImageFileRepository imageFileRepository;
	private final FileUtils fileUtils;
	
	@Override
	public ImageFile saveImage(MultipartFile file) {
		if (file != null) {
			ImageFile imageFile = fileUtils.fileUpload(file);
			if (imageFile != null) {
				ImageFile savedImageFile = imageFileRepository.save(imageFile);
				return savedImageFile;
			}
		}
		return null;
	}
	
	@Override
	public FileDTO getImageByImageId(Long id) {
		ImageFile image = imageFileRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("해당 아이디에 맞는 파일 없음"));
		return FileDTO.toDTO(image);
	}
}
