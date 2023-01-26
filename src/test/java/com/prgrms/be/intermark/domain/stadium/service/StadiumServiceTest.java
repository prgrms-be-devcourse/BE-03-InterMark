package com.prgrms.be.intermark.domain.stadium.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prgrms.be.intermark.domain.stadium.model.Stadium;
import com.prgrms.be.intermark.domain.stadium.repository.StadiumRepository;

@ExtendWith(MockitoExtension.class)
class StadiumServiceTest {

	@InjectMocks
	private StadiumService stadiumService;

	@Mock
	private StadiumRepository stadiumRepository;

	@Nested
	@DisplayName("findById")
	class FindById {

		@Test
		@DisplayName("성공 - id 로 경기장을 조회하면 Stadium 엔티티 반환한다.")
		void findByIdSuccess() {
			// given
			Stadium stadium = Stadium.builder()
				.name("예술의 전당")
				.address("서울특별시")
				.imageUrl("abcefg")
				.build();

			when(stadiumRepository.findById(anyLong())).thenReturn(Optional.of(stadium));

			// when
			Stadium findStadium = stadiumService.findById(anyLong());

			// then
			verify(stadiumRepository).findById(anyLong());
			assertThat(findStadium).usingRecursiveComparison().isEqualTo(stadium);
		}

		@Test
		@DisplayName("실패 - 없는 id 가 입력되면 EntityNotFoundException 반환한다.")
		void findByIdFail() {
			// given
			when(stadiumRepository.findById(anyLong())).thenReturn(Optional.empty());

			// when & then
			assertThatThrownBy(() -> stadiumService.findById(anyLong()))
				.isInstanceOf(EntityNotFoundException.class);
		}
	}
}