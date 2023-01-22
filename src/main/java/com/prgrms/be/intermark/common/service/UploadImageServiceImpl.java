package com.prgrms.be.intermark.common.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.prgrms.be.intermark.common.dto.ImageResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UploadImageServiceImpl implements UploadImageService {

	@Value("${file.images}")
	private String savePath;

	@Override
	public ImageResponseDTO uploadImage(MultipartFile multipartFile) {
		if (multipartFile.isEmpty()) {
			throw new IllegalArgumentException();
		}

		String originalFilename = multipartFile.getOriginalFilename();
		String savedFileName = createSavedFileName(originalFilename);

		try {
			multipartFile.transferTo(new File(getFullPath(savedFileName)));
		} catch (IOException e) {
			throw new RuntimeException("이미지를 업로드할 수 없습니다.", e);
		}

		return ImageResponseDTO.builder()
			.originalFileName(originalFilename)
			.path(getFullPath(savedFileName))
			.build();
	}

	@Override
	public List<ImageResponseDTO> uploadImages(List<MultipartFile> multipartFiles) {
		return multipartFiles.stream()
			.map(this::uploadImage)
			.toList();
	}

	public String getFullPath(String savedFileName) {
		return savePath + savedFileName;
	}

	private String createSavedFileName(String originalFileName) {
		String uuid = UUID.randomUUID().toString();
		return uuid + "." + extractExtension(originalFileName);
	}

	private String extractExtension(String originalFileName) {
		int beforeExtensionIndex = originalFileName.lastIndexOf(".");
		return originalFileName.substring(beforeExtensionIndex + 1);
	}
}
