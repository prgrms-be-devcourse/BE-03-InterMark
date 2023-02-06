package com.prgrms.be.intermark.common.service.page;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PageService {

    public PageRequest getPageRequest(Pageable pageable, int totalDataNumber) {
        int pageNumber = Math.min(pageable.getPageNumber(), totalDataNumber / pageable.getPageSize() - 1);
        if (pageNumber < 0) {
            pageNumber = 0;
        }
        return PageRequest.of(pageNumber, pageable.getPageSize());
    }
}
