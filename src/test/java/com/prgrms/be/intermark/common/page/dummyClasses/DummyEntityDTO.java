package com.prgrms.be.intermark.common.page.dummyClasses;

import java.util.StringJoiner;

public class DummyEntityDTO {
    String data;

    public DummyEntityDTO(String data) {
        this.data = data;
    }

    public static DummyEntityDTO toDto(DummyEntity dummyEntity) {
        return new DummyEntityDTO(dummyEntity.data);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DummyEntityDTO.class.getSimpleName() + "[", "]")
                .add("data='" + data + "'")
                .toString();
    }
}
