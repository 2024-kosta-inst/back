package com.kosta.service;

import org.springframework.web.multipart.MultipartFile;

import com.kosta.domain.FileDTO;
import com.kosta.entity.ImageFile;

public interface ImageFileService {
	ImageFile saveImage(MultipartFile file);
	
	FileDTO getImageByImageId(Long id);
}
