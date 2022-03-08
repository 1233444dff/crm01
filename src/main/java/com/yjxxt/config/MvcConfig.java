package com.yjxxt.config;

import interceptors.NoLoginInterceptors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Bean
    public NoLoginInterceptors noLoginInterceptors(){
        return  new NoLoginInterceptors();
    }
    @Override
    public  void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(noLoginInterceptors()).addPathPatterns("/**")
                .excludePathPatterns("/index","/user/login","/css/**","/images/**","/js/**","/lib/**");

    }

}
