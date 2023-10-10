
package com.zhuang.filestorage.enums;

import java.util.Arrays;

public enum FileDetailStatus {

    INTI(0, "未提交"),
    SUBMITTED(1, "已提交");

    private Integer value;
    private String name;

    FileDetailStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public static FileDetailStatus getByValue(Integer value) {
        return Arrays.stream(FileDetailStatus.values()).filter(c -> c.getValue().equals(value)).findFirst().orElse(null);
    }
}
