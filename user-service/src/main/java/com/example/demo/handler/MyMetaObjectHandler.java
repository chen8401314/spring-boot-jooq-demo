package com.example.demo.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.example.demo.context.ThreadLocalContextAccessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component // 注入bean
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * @Description: 新增时填充
     * @auther: zpq
     * @date: 2020/11/2 2:26 下午
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        // this.setFieldValByName(字段名, 内容, metaObject：用这个对象进行填充用的，称为元数据对象);
        log.info("添加时间");
        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("createId", ThreadLocalContextAccessor.getUserID(), metaObject);

        // ================setFieldValByName 方法内的内容，有兴趣可以进去看一看，中文注释的==================================================
        /// **
        // * 通用填充
        // *
        // * @param fieldName  java bean property name
        // * @param fieldVal   java bean property value
        // * @param metaObject meta object parameter
        // */
        // default MetaObjectHandler setFieldValByName(String fieldName, Object fieldVal, MetaObject
        // metaObject) {
        //  if (Objects.nonNull(fieldVal) && metaObject.hasSetter(fieldName)) {
        //    metaObject.setValue(fieldName, fieldVal);
        //  }
        //  return this;
        // }
    }

    /**
     * * @Description: 更新时填充
     *
     * @auther: zpq
     * @date: 2020/11/2 11:37 上午
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("修改时间");
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateId", ThreadLocalContextAccessor.getUserID(), metaObject);
    }
}