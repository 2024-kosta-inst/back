package com.kosta.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ImageFile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	private Long id;
	
	@Column(nullable = false, name = "original_name")
	private String originalName;
	
	@Column(nullable = false, name = "saved_name")
	private String savedName;
	
	@Column(nullable = false, name = "file_size")
	private Long fileSize;

	@Builder
	public ImageFile(String originalName, String savedName, Long fileSize) {
		this.originalName = originalName;
		this.savedName = savedName;
		this.fileSize = fileSize;
	}
}
