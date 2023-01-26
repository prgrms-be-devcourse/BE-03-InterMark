package com.prgrms.be.intermark.common.dto.page;

public enum PageListIndexSize {
    MUSICAL_LIST_INDEX_SIZE(10),
    TICKET_LIST_INDEX_SIZE(10);

    private final int size;

    PageListIndexSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
