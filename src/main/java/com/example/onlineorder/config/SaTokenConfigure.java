package com.example.onlineorder.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 1. 放行：登录接口 + 公开的菜品浏览 + Swagger 文档，其余都要登录
            SaRouter.match("/**")
                    .notMatch("/api/auth/**", "/api/dishes/**",
                              "/swagger-ui/**", "/swagger-ui.html",
                              "/v3/api-docs/**", "/v3/api-docs")
                    .check(r -> StpUtil.checkLogin());

            // 2. 管理员专属：/api/admin/** 必须是 admin 角色
            SaRouter.match("/api/admin/**")
                    .check(r -> StpUtil.checkRole("admin"));
        })).addPathPatterns("/**");
    }
}
