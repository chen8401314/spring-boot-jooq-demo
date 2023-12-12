/**
* @Title:
* @Description:
* @Author: chenx
* @Date: 2023/7/14
*/
package com.common.service.util.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.CellExtra;
import com.common.service.handler.OperationException;
import com.common.service.util.excel.read.ExcelRule;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
* @Title:
* @Description:
* @Author: chenx
* @Date: 2023/7/14
*/
@Slf4j
@RequiredArgsConstructor
public class ExcelReadListener<T> extends AnalysisEventListener<T> {

    @NonNull
    @Getter
    private final ExcelRule<T> excelRule;

    private Map<Integer, String> errorMap = new HashMap<>();

    private boolean readCompleted;

    @Getter
    private final Map<Integer, String> headerMap;

    private boolean initHeader = false;

    private int headerNum = 0;

    public ExcelReadListener(ExcelRule<T> excelRule) {
        this.excelRule = excelRule;
        this.headerMap = new HashMap<>(8);
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        //收集所有头部然后进行判断
        if (!initHeader) {
            excelRule.handleHeaderData(headerMap);
            initHeader = true;
        }
        //判断是否添加完成
        if (!readCompleted) {
            excelRule.excelHandleData(data, context);
        }

        if (excelRule.endData(data, context)) {
            String errorMsg = ExcelValidatorHelper.validate(data);
            if (StringUtils.isNotBlank(errorMsg)) {
                // 如果不需要捕获全部的错误信息,则读取到一个错误后往上抛出操作异常,目前读取全部错误信息
                errorMap.put(context.readRowHolder().getRowIndex(), errorMsg);
            }
        }

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        //发送读取完成事件
        excelRule.excelReadSuccessEvent(context);
        //清楚sheet的资源数据
        excelRule.clearHeaderData();

        if (!errorMap.isEmpty()) {
            StringBuilder message = new StringBuilder();
            errorMap.forEach((k, v) -> {
                        if (message.length() > 0) {
                            // 换行拼接信息
                            message.append(System.getProperty("line.separator"));
                        }
                        message.append(String.format("第[%s]行数据不正确:%s", k, v));
                    }
            );
            throw new OperationException(message.toString());
        }
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {

        Map<Integer, String> excelHeadMap = Maps.filterValues(headMap, StringUtils::isNotBlank);

        this.headerMap.put(headerNum++, Joiner.on(",").withKeyValueSeparator("=").join(excelHeadMap));
    }


    @Override
    public boolean hasNext(AnalysisContext context) {
        if (readCompleted) {
            return false;
        }
        if (excelRule.endCondition(context)) {
            postSuccess(context);
        }
        return true;
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        if (CellExtraTypeEnum.MERGE == extra.getType()) {
            excelRule.setExtra(extra, context);
        }
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
            String errorMsg = String.format("第%s行，第%s列解析异常，错误数据为:%s", excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex(), excelDataConvertException.getCellData());
            log.error(errorMsg);
            throw new OperationException(errorMsg);
        }
        //失败后需要清空内存的集合
        excelRule.clearData();
        excelRule.clearHeaderData();
    }

    private void postSuccess(AnalysisContext context) {
        //通知 excelRule发布数据存储
        readCompleted = true;
        doAfterAllAnalysed(context);
    }

}
