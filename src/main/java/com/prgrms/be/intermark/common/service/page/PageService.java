package com.prgrms.be.intermark.common.service.page;

import com.prgrms.be.intermark.domain.ticket.model.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PageService {

    public PageRequest getPageRequest(Pageable pageable, int totalDataNumber) {
        int pageNumber = Math.min(pageable.getPageNumber(), totalDataNumber / pageable.getPageSize() - 1);
        return PageRequest.of(pageNumber, pageable.getPageSize());
    }
}
