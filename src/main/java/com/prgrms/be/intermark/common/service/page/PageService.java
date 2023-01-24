package com.prgrms.be.intermark.common.service.page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PageService {

    public PageRequest getPageRequest(Pageable pageable, int totalDataNumber) {
        int pageNumber = Math.min(pageable.getPageNumber(), totalDataNumber / pageable.getPageSize() - 1);
        return PageRequest.of(pageNumber, pageable.getPageSize());
    }
}
