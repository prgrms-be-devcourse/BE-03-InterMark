package com.prgrms.be.intermark.domain.musical.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical_image.model.MusicalDetailImage;
import com.prgrms.be.intermark.domain.musical_image.repository.MusicalDetailImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicalDetailImageUploadService {

	private final MusicalDetailImageRepository musicalDetailImageRepository;

	@Value("${file.detailImages}")
	private String savePath;

	public void uploadImage(MultipartFile multipartFile, Musical musical) {
		if (multipartFile.isEmpty()) {
			throw new IllegalArgumentException();
		}

		String originalFilename = multipartFile.getOriginalFilename();
		String savedFileName = createSavedFileName(originalFilename);

		try {
			multipartFile.transferTo(new File(getFullPath(savedFileName)));
		} catch (IOException e) {
			throw new RuntimeException("상세 이미지를 업로드할 수 없습니다.", e);
		}

		MusicalDetailImage musicalDetailImage = MusicalDetailImage.builder()
			.musical(musical)
			.originalFileName(originalFilename)
			.imageUrl(getFullPath(savedFileName))
			.build();

		musicalDetailImageRepository.save(musicalDetailImage);
	}

	public void uploadFiles(List<MultipartFile> multipartFiles, Musical musical) {
		multipartFiles.forEach(multipartFile -> {
			uploadImage(multipartFile, musical);
		});
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
