/*
 *  Copyright (C) 2023 Hong Qiaowei <hongqiaowei@163.com>. All rights reserved.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.example.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.SpringBoot27;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.Ordered;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * unit test: WebMvcTests
 * @author Hong Qiaowei
 */
@Configuration
@ComponentScan(
        basePackages = SpringBoot27.ROOT_PKG,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {Controller.class}),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {ControllerAdvice.class})
        }
)
@EnableWebMvc
// @EnableSpringDataWebSupport
public class WebMvcConfig implements WebMvcConfigurer {

    // 用spring boot内置的ObjectMapper，参JacksonAutoConfiguration
    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Bean
    public SB2ErrorController sb2ErrorController(ServerProperties serverProperties, ErrorAttributes errorAttributes, ObjectProvider<ErrorViewResolver> errorViewResolvers) {
        return new SB2ErrorController(errorAttributes, serverProperties.getError(), errorViewResolvers.orderedStream().collect(Collectors.toList()));
    }

    // @Bean
    // public FilterRegistrationBean<CORSFilter> corsFilterRegistration() {
    //     FilterRegistrationBean<CORSFilter> reg = new FilterRegistrationBean<>();
    //     reg.setFilter(new CORSFilter());
    //     reg.addUrlPatterns("/*");
    //     reg.setName("CORSFilter");
    //     reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
    //     return reg;
    // }

    @Bean
    public FilterRegistrationBean<LogFilter> logFilterRegistration() {
        FilterRegistrationBean<LogFilter> reg = new FilterRegistrationBean<>();
        // reg.setDispatcherTypes(DispatcherType.REQUEST);
        reg.setFilter(new LogFilter());
        reg.addUrlPatterns("/*");
        reg.setName("LogFilter");
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }

    /**
     * 自定义httpMessageConverters替换spring boot内置的，仅支持json、表单、string消息体
     */
    @Bean
    public List<HttpMessageConverter<?>> httpMessageConverters() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

        StringHttpMessageConverter stringMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringMessageConverter.setWriteAcceptCharset(false);
        messageConverters.add(stringMessageConverter);

        FormHttpMessageConverter formMessageConverter = new FormHttpMessageConverter();
        messageConverters.add(formMessageConverter);

        MappingJackson2HttpMessageConverter jacksonMessageConverter = new MappingJackson2HttpMessageConverter();
        jacksonMessageConverter.setObjectMapper(objectMapper);
        List<MediaType> mediaTypes = Collections.singletonList(MediaType.APPLICATION_JSON);
        jacksonMessageConverter.setSupportedMediaTypes(mediaTypes);
        messageConverters.add(jacksonMessageConverter);

        return messageConverters;
    }

    /* @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping      ("/**")
//              .allowCredentials(true)  // 允许发送cookie
                .allowedOrigins  ("*")   // 放行哪些原始域
                .allowedMethods  ("OPTIONS, POST, GET, PUT, DELETE, HEAD") // 放行哪些请求方法
                .allowedHeaders  ("*")
                .exposedHeaders  ("*");
    } */

    /**
     * 增加HandlerMethodArgumentResolver，整合spring data的分页功能
     * @param methodArgumentResolvers initially an empty list
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> methodArgumentResolvers) {
        SortHandlerMethodArgumentResolver sr = new SortHandlerMethodArgumentResolver();
        methodArgumentResolvers.add(sr);
        PageableHandlerMethodArgumentResolver pr = new PageableHandlerMethodArgumentResolver(sr);

        pr.setPageParameterName("pageNo");
        pr.setSizeParameterName("pageSize");
        // 以上配置后，可像http://host:port/app/controller?pageSize=10&pageNo=6请求接口

        methodArgumentResolvers.add(pr);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> cs) {
        cs.addAll(httpMessageConverters());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//      registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(31556926);
//      registry.addResourceHandler("/img/**").addResourceLocations("/img/").setCachePeriod(31556926);
//      registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(31556926);

        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/").setCachePeriod(31556926);
    }

    // @Override
    // public void configureDefaultServletHandling(DefaultServletHandlerConfigurer Configure) {
    //     Configure.enable();
    // }

    /**
     * 按官方建议，配置特定的Executor处理异步请求，同时复用{@link org.example.config.TaskExecutorConfig}中的ThreadPoolTaskExecutor
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(threadPoolTaskExecutor);
    }

}
