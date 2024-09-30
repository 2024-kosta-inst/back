package com.kosta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

	// application.yml 파일의 location 정보 가져오기
	@Value("${spring.upload.location}")
	private String uploadPath;
		
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
			.addMapping("/**")
			.allowedOrigins("http://localhost:3000", "http://172.30.1.30:3000", "http://192.168.135.128", "http://192.168.135.128:80")
			.allowedMethods("OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
			.allowedHeaders("*") // 모든 헤더 허용
            .allowCredentials(true); // 자격 증명 허용 (쿠키, 인증 정보)
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
			.addResourceHandler("/img/**")
			.addResourceLocations("file:"+uploadPath + "\\");
	}
	
}
