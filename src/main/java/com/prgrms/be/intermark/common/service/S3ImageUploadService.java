package com.prgrms.be.intermark.common.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.prgrms.be.intermark.common.dto.ImageResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@Primary
@RequiredArgsConstructor
public class S3ImageUploadService implements ImageUploadService {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket.name}")
	private String bucketName;

	@Override
	public ImageResponseDTO uploadImage(MultipartFile multipartFile, String subPath) {

		if (multipartFile.isEmpty()) {
			throw new IllegalArgumentException("이미지가 없습니다.");
		}

		String originalFilename = multipartFile.getOriginalFilename();
		String savedFileName = createSavedFileName(originalFilename);
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(multipartFile.getContentType());

		try {
			amazonS3.putObject(new PutObjectRequest(bucketName, subPath + savedFileName, multipartFile.getInputStream(),
				objectMetadata));
		} catch (IOException e) {
			throw new IllegalStateException("이미지를 업로드할 수 없습니다.");
		}

		String savedFileS3Url = amazonS3.getUrl(bucketName, subPath + savedFileName).toString();

		return ImageResponseDTO.builder()
			.originalFileName(multipartFile.getOriginalFilename())
			.path(savedFileS3Url)
			.build();
	}

	@Override
	public List<ImageResponseDTO> uploadImages(List<MultipartFile> multipartFiles, String dir) {
		return multipartFiles.stream()
			.map(multipartFile -> uploadImage(multipartFile, dir))
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
}
