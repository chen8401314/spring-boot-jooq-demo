/**
* @Title:
* @Description:
* @Author: chenx
* @Date: 2023/7/14
*/
package com.common.service.util.excel.event;

import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
* @Title:
* @Description:
* @Author: chenx
* @Date: 2023/7/14
*/
public class ReadExcelEvent extends ApplicationEvent {

    public ReadExcelEvent(List dataList) {
        super(dataList);
    }
}
