package com.sky.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;

import java.text.SimpleDateFormat;
import java.util.List;

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

    /*
    * 设置扩展消息转换器
    * 用于：全剧统一时间日期格式化
    * */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("Extending message converters...");
        // 1. 创建一个消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 2. 为消息转换器设置一个对象转换器，可以将Java对象序列化转换为JSON，反之亦然
        // 需要的对象转换器位于common - json模块中
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // 3. 将上面的消息转换器对象追加到mvc框架的转换器集合中，也就是行参中的converters
        // 注意：由于默认自带了一些转换器，所以以上自定义的转换器需要放在List的最前面
        converters.add(0, messageConverter);
    }
}
