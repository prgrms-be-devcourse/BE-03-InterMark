package com.prgrms.be.intermark.domain.musical.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.prgrms.be.intermark.domain.musical.model.Musical;
import com.prgrms.be.intermark.domain.musical_image.model.MusicalImage;
import com.prgrms.be.intermark.domain.musical_image.repository.MusicalImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FIleUploadService {

	private final MusicalImageRepository musicalImageRepository;
	private String savePath = "D://바탕화면/file/";

	public String uploadFile(MultipartFile multipartFile, Musical musical) throws IOException {
		if (multipartFile.isEmpty()) {
			throw new IllegalArgumentException();
		}

		String originalFilename = multipartFile.getOriginalFilename();
		String savedFileName = createSavedFileName(originalFilename);
		multipartFile.transferTo(new File(getFullPath(savedFileName)));

		MusicalImage musicalImage = MusicalImage.builder()
			.musical(musical)
			.originalFileName(originalFilename)
			.imageUrl(getFullPath(savedFileName))
			.build();

		MusicalImage savedMusicalImage = musicalImageRepository.save(musicalImage);
		return savedMusicalImage.getImageUrl();
	}

	public void uploadFiles(List<MultipartFile> multipartFiles, Musical musical) {
		multipartFiles.forEach(multipartFile -> {
			try {
				uploadFile(multipartFile, musical);
			} catch (IOException e) {
				throw new RuntimeException();
			}
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
