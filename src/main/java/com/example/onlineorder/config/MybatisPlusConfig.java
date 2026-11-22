package com.example.onlineorder.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类。
 * <p>
 * 1) {@code @MapperScan} 扫描 Mapper 接口所在的包，之后 Mapper 接口就不用逐个再加 {@code @Mapper} 注解了；
 * 2) 注册分页插件，让 {@code BaseMapper} 的分页方法（{@code selectPage} 等）真正生效。
 */
@Configuration
@MapperScan("com.example.onlineorder.mapper")
public class MybatisPlusConfig {

    /**
     * 分页插件。MyBatis-Plus 3.4.3 之后统一用 MybatisPlusInterceptor + 内部拦截器的写法。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 指定数据库类型为 MySQL，分页插件会根据它生成对应的 LIMIT 分页语句
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
