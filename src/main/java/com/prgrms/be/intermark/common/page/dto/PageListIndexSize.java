package com.prgrms.be.intermark.common.page.dto;

public enum PageListIndexSize {
    ADMIN_PERFORMANCE_LIST_INDEX_SIZE(10);

    private final int size;

    PageListIndexSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
