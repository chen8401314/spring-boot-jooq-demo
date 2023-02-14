package com.huagui.common.base.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApprovalEnum {
    /**
     * 审批流程
     */
    NONE(0, "空"), NO_AUDIT(1, "未审核"), PENDING_AUDIT(2, "待审核"),
    AUDITED(3, "已审核"), NO_APPROVAL(4, "未审批"), PENDING_APPROVAL(5, "待审批"),
    APPROVED(6, "已审批");

    @Getter
    private int key;
    @Getter
    private String value;

    ApprovalEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }

    @JsonCreator
    public static ApprovalEnum fromKey(@JsonProperty(value = "key",required=false)int i) {
        for (ApprovalEnum s : ApprovalEnum.values()) {
            if (s.key == i) {
                return s;
            }
        }
        return NONE;
    }

    @JsonCreator
    public static ApprovalEnum fromKeys(int i) {
        for (ApprovalEnum s : ApprovalEnum.values()) {
            if (s.key == i) {
                return s;
            }
        }
        return NONE;
    }


}
