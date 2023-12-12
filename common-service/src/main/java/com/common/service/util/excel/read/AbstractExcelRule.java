/**
* @Title:
* @Description:
* @Author: chenx
* @Date: 2023/7/14
*/
package com.common.service.util.excel.read;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.common.service.util.excel.event.ReadExcelEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.*;

/**
* @Title:
* @Description:
* @Author: chenx
* @Date: 2023/7/14
*/
@Slf4j
public abstract class AbstractExcelRule<T> implements ExcelRule<T>, ApplicationEventPublisherAware {

    private ApplicationEventPublisher eventPublisher;
    private Collection<HeaderKey> headerNames;
    private Class<T> dataClass;
    private List<T> dataList = new ArrayList<>();
    /**
     * 单元格的额外信息 (批注  超链接 合并单元格)
     * 目前只添加了 '合并单元格' 处理
     */
    private List<CellExtra> extraList = new ArrayList<>();
    private Map<String, String> headerMap = new HashMap<>(8);
    private int headerNum;
    private int sheetIndex;
    private String sheetName;

    protected AbstractExcelRule(Class<T> dataClass, int sheetIndex, int headerNum) {
        this.dataClass = dataClass;
        this.headerNum = headerNum;
        this.sheetIndex = sheetIndex;
    }

    @Override
    public int headerNum() {
        return headerNum;
    }

    @Override
    public int sheetIndex() {
        return sheetIndex;
    }

    @Override
    public String getSheetName() {
        return sheetName;
    }

    @Override
    public Class<T> dataClass() {
        return dataClass;
    }

    public List<CellExtra> getExtraList() {
        return extraList;
    }

    @Override
    public ReadExcelEvent getReadEvent(List dataList) {
        return new ReadExcelEvent(dataList);
    }

    @Override
    public void excelHandleData(T data, AnalysisContext context) {
        if (handleData(data, context)) {
            dataList.add(data);
        }
    }

    @Override
    public void setExtra(CellExtra extra, AnalysisContext context) {
        extraList.add(extra);
    }

    @Override
    public void handleHeaderData(Map<Integer, String> headerMap) {
        //验证excel 头是否合法 不合法直接抛出异常

    }

    @Override
    public void excelReadSuccessEvent(AnalysisContext context) {
        clearHeaderData();
        this.sheetName = context.readSheetHolder().getSheetName();
        if (CollectionUtils.size(dataList) > 0 && eventPublisher != null) {
            log.info("sheet header :{}", headerMap);
            eventPublisher.publishEvent(getReadEvent(dataList));
        }
    }

    @Override
    public List<T> excelReadData() {
        clearHeaderData();
        return dataList;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        eventPublisher = applicationEventPublisher;
    }

    /**
     * 读取excel表格什么时候停止
     *
     * @param context
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean endCondition(AnalysisContext context) {
        Object result = context.readRowHolder().getCurrentRowAnalysisResult();
        // 头部解析判断
        if (result instanceof LinkedHashMap) {
            LinkedHashMap<Integer, WriteCellData<?>> linkedHashMap = (LinkedHashMap<Integer, WriteCellData<?>>) result;
            return linkedHashMap.size() > 0 && endCondition(linkedHashMap);
        }
        return false;
    }

    /**
     * 头部解析判断 默认没有结束
     *
     * @param data
     * @return
     */
    protected boolean endCondition(LinkedHashMap<Integer, WriteCellData<?>> data) {
        return false;
    }

    /**
     * 判断数据是否需要进行校验、忽略、或者存储
     *
     * @param data
     * @param context
     * @return
     */
    protected abstract boolean handleData(T data, AnalysisContext context);


    @Override
    public boolean endData(T data, AnalysisContext context) {
        return handleData(data, context);
    }

    @Override
    public void clearHeaderData() {
        headerMap.clear();
    }

    @Override
    public void clearData() {
        dataList.clear();
    }

    @RequiredArgsConstructor(staticName = "of")
    protected static class HeaderKey {

        @NonNull
        private Integer key;

        @NonNull
        private String dataName;
    }
}
