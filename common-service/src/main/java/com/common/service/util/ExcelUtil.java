/**
* @Title:
* @Description:
* @Author: chenx
* @Date: 2023/7/14
*/
package com.common.service.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.common.service.util.excel.model.AbstractReaderBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
* @Title:
* @Description:
* @Author: chenx
* @Date: 2023/7/14
*/
@Slf4j
public class ExcelUtil {
    /**
     * 所有inputStream导入的入口
     */
    public static AbstractReaderBuilder read(InputStream inputStream) {
        return new AbstractReaderBuilder(EasyExcel.read(inputStream).extraRead(CellExtraTypeEnum.MERGE).build());
    }

    /**
     * 所有filePath导入的入口
     */
    public static AbstractReaderBuilder read(String filePath) {
        return new AbstractReaderBuilder(EasyExcel.read(filePath).extraRead(CellExtraTypeEnum.MERGE).build());
    }

}
