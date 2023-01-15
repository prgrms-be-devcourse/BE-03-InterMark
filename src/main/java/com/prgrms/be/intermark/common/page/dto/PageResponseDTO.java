package com.prgrms.be.intermark.common.page.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

@Getter
public class PageResponseDTO<E, DTO> {
    private final List<DTO> data;
    private final boolean isPrev;
    private final boolean isNext;
    private final List<Integer> nowPageNumbers;
    private final int nowPage;

    public PageResponseDTO(Page<E> page, Function<E, DTO> entityToDtoFunction, PageListIndexSize pageListIndexSize) {
        int startPageNumber = calculateStartPageNumber(page, pageListIndexSize);
        int endPageNumber = calculateEndPageNumber(page, startPageNumber, pageListIndexSize);

        this.data = page.stream()
                .map(entityToDtoFunction)
                .toList();

        this.nowPageNumbers = IntStream.rangeClosed(startPageNumber, endPageNumber)
                .boxed()
                .toList();

        this.nowPage = calculateNowPage(page);
        this.isNext = checkIsNext(page, startPageNumber, pageListIndexSize);
        this.isPrev = checkIsPrev(startPageNumber);
    }

    private int calculateStartPageNumber(Page<E> page, PageListIndexSize pageListIndexSize) {
        return pageListIndexSize.getSize() * ((page.getNumber()) / pageListIndexSize.getSize()) + 1;
    }

    private int calculateEndPageNumber(Page<E> page, int startPageNumber, PageListIndexSize pageListIndexSize) {
        return Math.min(page.getTotalPages(), startPageNumber + pageListIndexSize.getSize() - 1);
    }

    private int calculateNowPage(Page<E> page) {
        return page.getNumber() + 1;
    }

    private boolean checkIsNext(Page<E> page, int startPageNumber, PageListIndexSize pageListIndexSize) {
        return startPageNumber + pageListIndexSize.getSize() - 2 < page.getTotalPages();
    }

    private boolean checkIsPrev(int startPageNumber) {
        return startPageNumber > 1;
    }
}
