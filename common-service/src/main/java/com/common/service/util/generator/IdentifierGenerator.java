package com.common.service.util.generator;


import com.common.service.util.IdWorker;

public interface IdentifierGenerator {

    Number nextId(Object entity);

    default String nextUUID(Object entity) {
        return IdWorker.get32UUID();
    }
}
