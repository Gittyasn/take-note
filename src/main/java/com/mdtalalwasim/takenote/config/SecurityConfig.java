package com.mdtalalwasim.takenote.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import com.mdtalalwasim.takenote.security.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private JwtFilter jwtFilter;
	
	
	//Core interface which loads user-specific data.
	//It is used throughout the framework as a user DAO 
	//and is the strategy used by the DaoAuthenticationProvider.
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider dap = new DaoAuthenticationProvider();
		
		dap.setUserDetailsService(userDetailsService);
		dap.setPasswordEncoder(passwordEncoder());
		
		return dap;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http
			.csrf(csrf -> csrf.disable())
			// IF_REQUIRED: Spring creates a session only when needed (e.g., flash messages
			// on Thymeleaf redirects). JWT authentication still works because JwtFilter
			// reads the token from the Authorization header or JWT_TOKEN cookie directly.
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/auth/**", "/", "/register", "/signin", "/save-user", "/css/**", "/js/**", "/img/**").permitAll()
				.requestMatchers("/api/**").authenticated()
				.requestMatchers("/user/**").authenticated()
				.anyRequest().permitAll()
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/signin?logout")
				.deleteCookies("JWT_TOKEN")
			);
			// We remove default formLogin to manage auth mostly via API + JWT
			//.formLogin(form -> form...);
			
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
		
		
	}
	
	
	
	
	

	   
}
