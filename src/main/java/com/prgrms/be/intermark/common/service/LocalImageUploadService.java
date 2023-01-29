package com.prgrms.be.intermark.common.service;

import com.prgrms.be.intermark.common.dto.ImageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalImageUploadService implements ImageUploadService {

	@Value("${local.images.path}")
	private String localRootPath;

	@Override
	public ImageResponseDTO uploadImage(MultipartFile multipartFile, String subPath) {
		if (multipartFile.isEmpty()) {
			throw new IllegalArgumentException("이미지가 없습니다.");
		}

		String originalFilename = multipartFile.getOriginalFilename();
		String savedFileName = createSavedFileName(originalFilename);
		String savedFileLocalPath = getSavedFileLocalPath(subPath, savedFileName);
		File uploadImage = new File(savedFileLocalPath);

		try {
			multipartFile.transferTo(uploadImage);
		} catch (IOException e) {
			throw new IllegalArgumentException("이미지를 업로드할 수 없습니다.", e);
		}

		return ImageResponseDTO.builder()
			.originalFileName(originalFilename)
			.path(savedFileLocalPath)
			.build();
	}

	@Override
	public List<ImageResponseDTO> uploadImages(List<MultipartFile> multipartFiles, String subPath) {
		return multipartFiles.stream()
			.map(multipartFile -> uploadImage(multipartFile, subPath))
			.toList();
	}

	private String createSavedFileName(String originalFileName) {
		String uuid = UUID.randomUUID().toString();
		return uuid + "." + extractExtension(originalFileName);
	}

	private String extractExtension(String originalFileName) {
		int beforeExtensionIndex = originalFileName.lastIndexOf(".");
		return originalFileName.substring(beforeExtensionIndex + 1);
	}

	private String getSavedFileLocalPath(String subPath, String savedFileName) {
		return localRootPath + subPath + savedFileName;
	}
}
