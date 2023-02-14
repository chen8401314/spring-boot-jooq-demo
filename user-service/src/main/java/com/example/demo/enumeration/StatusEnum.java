package com.example.demo.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    /**
     *
     */
    VALID("可用"),
    INVALID("不可用");

    private String desc;

    @Override
    public String toString() {
        return this.name() + "-" + desc;
    }


}
