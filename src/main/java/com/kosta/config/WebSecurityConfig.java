package com.kosta.config;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.kosta.repository.UserRepository;
import com.kosta.security.JwtAuthenticationFilter;
import com.kosta.security.JwtAuthenticationService;
import com.kosta.security.JwtProperties;
import com.kosta.security.JwtProvider;
import com.kosta.security.LoginCustomAuthenticationFilter;
import com.kosta.util.TokenUtils;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
	private final UserDetailsService userDetailsService;
	private final UserRepository userRepository;
	private final JwtProperties	jwtProperties;
	
	// JWT PROVIDER 생성자 호출
	private JwtProvider jwtProvider() {
		return new JwtProvider(userDetailsService, jwtProperties);
	}
	
	// TokenUtils 생성자 호출
	private TokenUtils tokenUtils() {
		return new TokenUtils(jwtProvider());
	}
	
	// JwtAuthenticationService 생성자 호출
	private JwtAuthenticationService jwtAuthenticationService() {
		return new JwtAuthenticationService(tokenUtils(), userRepository);
	}
	
	// 인증 관리자 (AuthenticationManager) 설정
	@Bean
	AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(bCryptPasswordEncoder());
		return new ProviderManager(authProvider);
	}
	
	// 암호화 빈
	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	// HTTP 요청에 따른 보안 구성
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		// 경로 권한 설정
		http.authorizeHttpRequests(auth ->
			// 특정 URL 경로에 대해서는 인증 없이 접근 가능
			auth.requestMatchers(
				new AntPathRequestMatcher("/img/**"), // 이미지
				new AntPathRequestMatcher("/api/oauth/**"),
				new AntPathRequestMatcher("/api/auth/signup"), // 회원가입
				new AntPathRequestMatcher("/api/auth/duplicate"), // 이메일 중복체크
				new AntPathRequestMatcher("/api/auth/refresh-token"), // 토큰 재발급
				new AntPathRequestMatcher("/api/post/**", "GET")
			).permitAll()
			// AuthController 중 나머지들은 "ADMIN"만 가능
			.requestMatchers(
				new AntPathRequestMatcher("/api/auth")
			).hasRole("ADMIN")
			// 그 밖의 다른 요청들은 인증을 통과한(로그인한) 사용자라면 모두 접근할 수 있도록 한다.
			.anyRequest().authenticated()
		);
		
		// 무상태성 세션 관리
		http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		// 특정 경로(로그인)에 대한 필터 추가 [authenticationManager, jwtAuthenticationService 생성자를 호출해 매개변수로 등록]
		http.addFilterBefore(new LoginCustomAuthenticationFilter(authenticationManager(), jwtAuthenticationService()), UsernamePasswordAuthenticationFilter.class);
		
		// (토큰을 통해 검증할 수 있도록) 필터 추가 [jwtProvider 생성자를 호출해 매개변수로 등록]
		http.addFilterAfter(new JwtAuthenticationFilter(jwtProvider()), UsernamePasswordAuthenticationFilter.class);
		
		// HTTP 기본 설정
		http.httpBasic(HttpBasicConfigurer::disable);
		// CSRF 비활성화
		http.csrf(AbstractHttpConfigurer::disable);
		// CORS 설정
		http.cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()));
		
		return http.getOrBuild();
	}
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		return request -> {
			CorsConfiguration config = new CorsConfiguration();
			config.setAllowedHeaders(Collections.singletonList("*"));
			config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
			config.setAllowedOrigins(List.of("http://localhost:3000", "http://172.30.1.30:3000", "http://192.168.135.128", "http://192.168.135.128:80"));
			config.setAllowCredentials(true);
			return config;
		};
	}
}
