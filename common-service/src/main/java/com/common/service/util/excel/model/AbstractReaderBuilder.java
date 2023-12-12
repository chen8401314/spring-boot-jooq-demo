/**
* @Title:
* @Description:
* @Author: chenx
* @Date: 2023/7/14
*/
package com.common.service.util.excel.model;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.common.service.util.excel.read.ExcelRule;
import com.common.service.util.excel.listener.ExcelReadListener;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;
/**
* @Title:
* @Description:
* @Author: chenx
* @Date: 2023/7/14
*/
@NoArgsConstructor
public class AbstractReaderBuilder {

    @Schema(description = "event对象")
    private ApplicationEventPublisher eventPublisher;

    @Schema(description = "read对象")
    private ExcelReader read;

    public AbstractReaderBuilder(ExcelReader read) {
        this.read = read;
    }

    public AbstractReaderBuilder eventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        return this;
    }

    public <T extends ExcelRule> void doRead(List<T> rules) {
        // 文件处理规则
        for (ExcelRule rule : rules) {
            if (eventPublisher != null) {
                rule.setApplicationEventPublisher(eventPublisher);
            }
            ReadSheet readSheet = EasyExcel.readSheet(rule.sheetIndex())
                    .head(rule.dataClass())
                    .headRowNumber(rule.headerNum())
                    .registerReadListener(new ExcelReadListener(rule)).build();
            read.read(readSheet);
        }
        read.finish();
    }

    public void doRead(ExcelRule<?> rule) {
        doRead(Collections.singletonList(rule));
    }

}
