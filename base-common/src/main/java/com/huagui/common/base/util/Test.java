package com.huagui.common.base.util;

public class Test {

    public static void main(String[] args) throws Exception{

        ApprovalEnum approvalEnum = ApprovalEnum.APPROVED;

        System.out.println("args = [" + JsonObjectConverter.objectToJson(approvalEnum) + "]");
        String ss= "{\"key\":6,\"value\":\"已审批\"}";
        ApprovalEnum en = JsonObjectConverter.jsonToObject(ss, ApprovalEnum.class);

        System.out.println("args = [" + JsonObjectConverter.objectToJson(approvalEnum) + "]");
        Wrapper wrapper = new Wrapper(ApprovalEnum.AUDITED);
        System.out.println("args = [" + JsonObjectConverter.objectToJson(wrapper) + "]");
        ss = "{\"approval\":3}";

        System.out.println("args = [" + JsonObjectConverter.jsonToObject(ss, Wrapper.class) + "]");

    }


    static class Wrapper {
        ApprovalEnum approval;

        public Wrapper() {
        }

        public Wrapper(ApprovalEnum approval) {
            this.approval = approval;
        }
    }
}
