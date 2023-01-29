package com.prgrms.be.intermark.common.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.prgrms.be.intermark.common.dto.ImageResponseDTO;

public interface ImageUploadService {

	ImageResponseDTO uploadImage(MultipartFile multipartFile, String subPath);

	List<ImageResponseDTO> uploadImages(List<MultipartFile> multipartFiles, String subPath);
}