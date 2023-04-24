package com.huagui.service.util.generator;


import com.huagui.service.util.IdWorker;

public interface IdentifierGenerator {

    Number nextId(Object entity);

    default String nextUUID(Object entity) {
        return IdWorker.get32UUID();
    }
}
