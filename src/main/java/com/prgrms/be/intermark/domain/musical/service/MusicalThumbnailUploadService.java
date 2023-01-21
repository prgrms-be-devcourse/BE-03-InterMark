package com.prgrms.be.intermark.domain.musical.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.prgrms.be.intermark.domain.musical.model.MusicalThumbnail;
import com.prgrms.be.intermark.domain.musical.repository.MusicalThumbnailRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicalThumbnailUploadService {

	private final MusicalThumbnailRepository musicalThumbnailRepository;

	@Value("${file.thumbnail}")
	private String savePath;

	public MusicalThumbnail uploadThumbnail(MultipartFile multipartFile) {
		if (multipartFile.isEmpty()) {
			throw new IllegalArgumentException();
		}

		String originalFilename = multipartFile.getOriginalFilename();
		String savedFileName = createSavedFileName(originalFilename);

		try {
			multipartFile.transferTo(new File(getFullPath(savedFileName)));
		} catch (IOException e) {
			throw new RuntimeException("썸네일을 업로드할 수 없습니다.", e);
		}

		MusicalThumbnail musicalThumbnail = MusicalThumbnail.builder()
			.originalFileName(originalFilename)
			.imageUrl(getFullPath(savedFileName))
			.build();

		return musicalThumbnailRepository.save(musicalThumbnail);
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
