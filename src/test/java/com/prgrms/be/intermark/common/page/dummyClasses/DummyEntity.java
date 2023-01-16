package com.prgrms.be.intermark.common.page.dummyClasses;

import java.util.StringJoiner;

public class DummyEntity {
    String data;
    int value;

    public DummyEntity(String data, int value) {
        this.data = data;
        this.value = value;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DummyEntity.class.getSimpleName() + "[", "]")
                .add("data='" + data + "'")
                .add("value=" + value)
                .toString();
    }
}
