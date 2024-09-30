package com.kosta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kosta.entity.ImageFile;

@Repository
public interface ImageFileRepository extends JpaRepository<ImageFile, Long>{

}
