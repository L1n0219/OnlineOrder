package com.example.onlineorder.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                     // 1. 对所有接口生效
                .allowedOriginPatterns("*")            // 2. 允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 3. 允许的方法
                .allowedHeaders("*")                   // 4. 允许所有请求头
                .exposedHeaders("satoken")             // 5. 允许前端读取 satoken 这个响应头
                .allowCredentials(true)                // 6. 允许携带 cookie
                .maxAge(3600);                         // 7. 预检请求缓存 1 小时
    }
}
