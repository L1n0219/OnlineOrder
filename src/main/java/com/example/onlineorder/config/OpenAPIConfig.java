package com.example.onlineorder.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger 文档配置。
 * <p>
 * 核心作用：把 Sa-Token 的 token 声明成一个「API Key 请求头鉴权」，
 * 这样 Swagger UI 右上角会出现 Authorize 按钮，点开填入 satoken 后，
 * 所有「Try it out」发出的请求都会自动带上 satoken 请求头，从而通过鉴权。
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI() {
        // 定义一个 API Key 类型的鉴权方案：从请求头读取名为 satoken 的字段
        SecurityScheme scheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)   // 从请求头取 token
                .in(SecurityScheme.In.HEADER)       // 位置在 header
                .name("satoken");                   // header 名，和 Sa-Token 的 token-name 一致

        return new OpenAPI()
                .info(new Info()
                        .title("软件园食堂线上点单系统")
                        .description("Sa-Token 鉴权：登录后把返回的 token 填进 Authorize")
                        .version("1.0"))
                // 把上面的鉴权方案挂到文档里
                .components(new Components().addSecuritySchemes("satoken", scheme))
                // 默认所有接口都应用这个鉴权（只是声明，不会强制，不带也能测公开接口）
                .addSecurityItem(new SecurityRequirement().addList("satoken"));
    }
}
