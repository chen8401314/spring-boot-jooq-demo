package com.example.demo.service;

import com.example.demo.dto.TestDTO;
import com.example.demo.repository.TestRep;
import com.example.demo.request.QueryTestReq;
import com.common.service.dto.PageDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestServiceTest {

    @Mock
    private TestRep testRep;

    @InjectMocks
    private TestService testService;

    @Test
    void findPageByName_ShouldReturnPageDTO() {
        // 准备测试数据
        QueryTestReq req = new QueryTestReq();
        PageDTO<TestDTO> expectedPage = new PageDTO<>();
        
        // 模拟依赖行为
        when(testRep.findPageByName(req)).thenReturn(expectedPage);

        // 执行测试方法
        PageDTO<TestDTO> result = testService.findPageByName(req);

        // 验证结果
        assertEquals(expectedPage, result);
    }
} 