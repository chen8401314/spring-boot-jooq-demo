package com.example.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

import static com.example.common.context.ServiceContext.TOKEN_HEADER;

/**
* @Title: swagger配置
* @Description:
* @Author: chenx
* @Date: 2023/7/7
*/
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "数据底座服务API文档",
                description = "数据底座服务API文档", version = "1.0.0"
                //,license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"),
                //contact = @Contact(name = "RtxTitanV", url = "https://blog.csdn.net/RtxTitanV", email = "RtxTitanV@xxx.com")
        ),
        //externalDocs = @ExternalDocumentation(description = "参考文档", url = "https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations"),
        servers = @Server(url = "/user"),
        security = @SecurityRequirement(name = TOKEN_HEADER)
)
@SecurityScheme(name = TOKEN_HEADER, type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer", in = SecuritySchemeIn.HEADER)
public class SwaggerConfig {

}
