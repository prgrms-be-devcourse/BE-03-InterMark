package com.prgrms.be.intermark.domain.musical.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "musical_thumbnail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MusicalThumbnail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(name="original_file_name", nullable = false)
	private String originalFileName;

	@NotBlank
	@Column(name = "image_url", nullable = false, length = 2000)
	private String imageUrl;

	@Builder
	public MusicalThumbnail(String originalFileName, String imageUrl) {
		this.originalFileName = originalFileName;
		this.imageUrl = imageUrl;
	}
}
