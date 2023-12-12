/**
* @Title:
* @Description:
* @Author: chenx
* @Date: 2023/7/14
*/
package com.common.service.util.excel.read;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.CellExtra;
import com.common.service.util.excel.event.ReadExcelEvent;
import org.springframework.context.ApplicationEventPublisherAware;

import java.util.List;
import java.util.Map;

/**
* @Title:
* @Description:
* @Author: chenx
* @Date: 2023/7/14
*/
public interface ExcelRule<T> extends ApplicationEventPublisherAware {

    /**
     * sheet 的对应class
     *
     * @return
     */
    Class<T> dataClass();

    /**
     * 标题行数
     *
     * @return
     */
    int headerNum();

    /**
     * sheetNo
     *
     * @return
     */
    int sheetIndex();

    /**
     * 获取excel name
     *
     * @return
     */
    String getSheetName();

    /**
     * 读取结束 发送的事件
     *
     * @return
     */
    void excelReadSuccessEvent(AnalysisContext context);

    /**
     * 读取结束 收集到的集合
     *
     * @return
     */
    List<T> excelReadData();

    /**
     * 添加excel附属信息
     */
    void setExtra(CellExtra extra, AnalysisContext context);

    /**
     * 失败后清楚临时存储资源
     */
    void clearData();

    /**
     * 失败后清楚临时存储资源
     */
    void clearHeaderData();

    /**
     * 每个sheet读取完后的事件
     * 需要发布的事件
     *
     * @return
     */
    ReadExcelEvent getReadEvent(List dataList);

    /**
     * 用于每行的数据验证
     *
     * @param data    数据
     * @param context context
     */
    void excelHandleData(T data, AnalysisContext context);

    /**
     * 用于标题的数据处理
     *
     * @param headerMap
     */
    void handleHeaderData(Map<Integer, String> headerMap);

    /**
     * 判断是否结束
     *
     * @param context
     * @return
     */
    boolean endCondition(AnalysisContext context);

    /**
     * 判断数据是否结束,与endCondition不同在于有数据逻辑
     *
     * @param data
     * @param context
     * @return
     */
    boolean endData(T data, AnalysisContext context);

}
