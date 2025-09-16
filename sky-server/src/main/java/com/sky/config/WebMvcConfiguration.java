package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("Started registering interceptors...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/login");
    }

    /**
     * 通过SpringDoc 生成API文档
     * 本Bean用于基础的API信息配置
     * @return
     */
    @Bean
    public OpenAPI customOpenAPI() {
        log.info("Start generating API documentation...");
        return new OpenAPI()
                .info(new Info()
                        .title("Sky Takeout API Documentation")
                        .version("V2.0")
                        .description("This is the API documentation for the Sky Takeout application."));
    }
    /*
    * 配置分组API文档
    * 本Bean用于admin端（后台管理端）接口的分组
    * */
    @Bean
    public GroupedOpenApi adminApi() {
        log.info("Start grouping admin APIs...");
        return GroupedOpenApi.builder()
                .group("AdminAPIs")
                .pathsToMatch("/admin/**") // 只包含/admin路径下的Controller(以及里面的接口方法)
                .build();
    }

    /**
     * 设置静态资源映射
     * Swagger UI所需的静态资源
     * @param registry
     */
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
