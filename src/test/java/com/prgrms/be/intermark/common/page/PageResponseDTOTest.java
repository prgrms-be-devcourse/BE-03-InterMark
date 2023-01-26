package com.prgrms.be.intermark.common.page;

import static org.assertj.core.api.AssertionsForInterfaceTypes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.prgrms.be.intermark.common.dto.page.dto.PageListIndexSize;
import com.prgrms.be.intermark.common.dto.page.dto.PageResponseDTO;
import com.prgrms.be.intermark.common.page.dummyClasses.DummyEntity;
import com.prgrms.be.intermark.common.page.dummyClasses.DummyEntityDTO;

class PageResponseDTOTest {

    int totalElementsSize = 50;
    List<DummyEntity> allEntities = new ArrayList<>();

    @BeforeEach
    void init() {
        for (int i = 0; i < totalElementsSize; i++) {
            allEntities.add(new DummyEntity("data" + i, i));
        }
    }

    @Test
    @DisplayName("PageResponseDTO 생성 시 DTO로 변환된 데이터 확인")
    void checkPageResponseDTOData() {

        // given
        int size = 5;
        int page = 1;
        Pageable pageable = PageRequest.of(page, size);

        Page<DummyEntity> dummyEntityPage = getDummyEntityPage(pageable);

        // when
        PageResponseDTO<DummyEntity, DummyEntityDTO> pageResponseDTOs = new PageResponseDTO<>(dummyEntityPage, DummyEntityDTO::toDto, PageListIndexSize.MUSICAL_LIST_INDEX_SIZE);

        // then
        assertThat(pageResponseDTOs.getData())
                .extracting("data")
                .contains(dummyEntityPage.getContent().get(0).getData())
                .contains(dummyEntityPage.getContent().get(1).getData())
                .contains(dummyEntityPage.getContent().get(2).getData())
                .contains(dummyEntityPage.getContent().get(3).getData())
                .contains(dummyEntityPage.getContent().get(4).getData());
    }

    @Test
    @DisplayName("PageResponseDTO 생성 시 표시되는 현재 페이지 리스트 데이터 확인")
    void checkPageResponseDTONowPageNumbers() {

        // given
        int size = 5;
        int page = 1;
        Pageable pageable = PageRequest.of(page, size);

        Page<DummyEntity> dummyEntityPage = getDummyEntityPage(pageable);

        List<Integer> nowPageNumbers = IntStream.rangeClosed(1, 10).boxed().toList();

        // when
        PageResponseDTO<DummyEntity, DummyEntityDTO> pageResponseDTOs = new PageResponseDTO<>(dummyEntityPage, DummyEntityDTO::toDto, PageListIndexSize.MUSICAL_LIST_INDEX_SIZE);

        // then
        assertThat(pageResponseDTOs.getNowPageNumbers()).isEqualTo(nowPageNumbers);
    }

    @Test
    @DisplayName("PageResponseDTO 생성 시 다음 페이지 리스트 유무 데이터 확인 : 다음 페이지 리스트가 없는 경우")
    void checkIsNextPageWhenNotExists() {

        // given
        int size = 10;
        int page = 1;
        Pageable pageable = PageRequest.of(page, size);

        Page<DummyEntity> dummyEntityPage = getDummyEntityPage(pageable);

        // when
        PageResponseDTO<DummyEntity, DummyEntityDTO> pageResponseDTOs = new PageResponseDTO<>(dummyEntityPage, DummyEntityDTO::toDto, PageListIndexSize.MUSICAL_LIST_INDEX_SIZE);

        // then
        assertThat(pageResponseDTOs.isNext()).isFalse();
    }

    @Test
    @DisplayName("PageResponseDTO 생성 시 다음 페이지 리스트 유무 데이터 확인 : 다음 페이지 리스트가 있는 경우")
    void checkIsNextPageWhenExists() {

        // given
        int size = 2;
        int page = 1;
        Pageable pageable = PageRequest.of(page, size);

        Page<DummyEntity> dummyEntityPage = getDummyEntityPage(pageable);

        // when
        PageResponseDTO<DummyEntity, DummyEntityDTO> pageResponseDTOs = new PageResponseDTO<>(dummyEntityPage, DummyEntityDTO::toDto, PageListIndexSize.MUSICAL_LIST_INDEX_SIZE);

        // then
        assertThat(pageResponseDTOs.isNext()).isTrue();
    }

    @Test
    @DisplayName("PageResponseDTO 생성 시 다음 페이지 리스트 유무 데이터 확인 : 이전 페이지 리스트가 없는 경우")
    void checkIsPrevPageWhenNotExists() {

        // given
        int size = 10;
        int page = 1;
        Pageable pageable = PageRequest.of(page, size);

        Page<DummyEntity> dummyEntityPage = getDummyEntityPage(pageable);

        // when
        PageResponseDTO<DummyEntity, DummyEntityDTO> pageResponseDTOs = new PageResponseDTO<>(dummyEntityPage, DummyEntityDTO::toDto, PageListIndexSize.MUSICAL_LIST_INDEX_SIZE);

        // then
        assertThat(pageResponseDTOs.isPrev()).isFalse();
    }

    @Test
    @DisplayName("PageResponseDTO 생성 시 다음 페이지 리스트 유무 데이터 확인 : 이전 페이지 리스트가 있는 경우")
    void checkIsPrevPageWhenExists() {

        // given
        int size = 2;
        int page = 11;
        Pageable pageable = PageRequest.of(page, size);

        Page<DummyEntity> dummyEntityPage = getDummyEntityPage(pageable);

        // when
        PageResponseDTO<DummyEntity, DummyEntityDTO> pageResponseDTOs = new PageResponseDTO<>(dummyEntityPage, DummyEntityDTO::toDto, PageListIndexSize.MUSICAL_LIST_INDEX_SIZE);

        // then
        assertThat(pageResponseDTOs.isPrev()).isTrue();
    }

    private Page<DummyEntity> getDummyEntityPage(Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allEntities.size());
        return new PageImpl<>(allEntities.subList(start, end), pageable, allEntities.size());
    }
}