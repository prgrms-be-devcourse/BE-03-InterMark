package com.prgrms.be.intermark.common.page;

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

    private static final int STANDARD_PAGE_LIST_NUMBER = 10;

    public PageResponseDTO(Page<E> page, Function<E, DTO> entityToDtoFunction) {

        int startPageNumber = calculateStartPageNumber(page);
        int endPageNumber = calculateEndPageNumber(page, startPageNumber);

        this.data = page.stream()
                .map(entityToDtoFunction)
                .toList();

        this.nowPageNumbers = IntStream.rangeClosed(startPageNumber, endPageNumber)
                .boxed()
                .toList();

        this.nowPage = calculateNowPage(page);
        this.isNext = checkIsNext(page, startPageNumber);
        this.isPrev = checkIsPrev(startPageNumber);
    }

    private int calculateStartPageNumber(Page<E> page) {
        return STANDARD_PAGE_LIST_NUMBER * ((page.getNumber()) / STANDARD_PAGE_LIST_NUMBER) + 1;
    }

    private int calculateEndPageNumber(Page<E> page, int startPageNumber) {
        return Math.min(page.getTotalPages(), startPageNumber + STANDARD_PAGE_LIST_NUMBER - 1);
    }

    private int calculateNowPage(Page<E> page) {
        return page.getNumber() + 1;
    }

    private boolean checkIsNext(Page<E> page, int startPageNumber) {
        return startPageNumber + STANDARD_PAGE_LIST_NUMBER - 2 < page.getTotalPages();
    }

    private boolean checkIsPrev(int startPageNumber) {
        return startPageNumber > 1;
    }
}
