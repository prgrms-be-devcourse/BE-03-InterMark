package com.prgrms.be.intermark.common.service;

import com.prgrms.be.intermark.common.dto.ImageResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageUploadService {

	ImageResponseDTO uploadImage(MultipartFile multipartFile, String subPath);

	List<ImageResponseDTO> uploadImages(List<MultipartFile> multipartFiles, String subPath);
}
