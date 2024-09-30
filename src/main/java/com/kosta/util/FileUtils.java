package com.kosta.util;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.kosta.entity.ImageFile;


@Component
public class FileUtils {
	
	// application.yml 파일의 location 정보 가져오기
	@Value("${spring.upload.location}")
	private String uploadPath;
	
	public ImageFile fileUpload(MultipartFile file) {
		try {
			// 원본 파일명 가져오기
			String originalFileName = file.getOriginalFilename();
			// 파일 크기 가져오기
			Long fileSize = file.getSize();
			// 새로운 파일명 만들어주기
			String savedFileName = UUID.randomUUID() + "_" + originalFileName;
			
			// 해당 경로에 파일 이미지 업로드
			InputStream inputStream = file.getInputStream();
			Path path = Paths.get(uploadPath).resolve(savedFileName);
			Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
			
			// 이상 없으면 새로운 ImageFile 객체 반환			
			return ImageFile.builder()
					.originalName(originalFileName)
					.savedName(savedFileName)
					.fileSize(fileSize)
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			// 이상 있으면 null 반환
			return null;
		}
	}
}
