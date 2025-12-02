package com.example.demo.config;


import com.example.demo.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final UserDetailsServiceImpl userDetailsService;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/admin/orderList", "/admin/order", "/admin/accountInfo")
						.hasAnyRole("EMPLOYEE", "MANAGER")
						.requestMatchers("/admin/product").hasRole("MANAGER")
						.anyRequest().permitAll()
				)
				.exceptionHandling(ex -> ex.accessDeniedPage("/403"))
				.formLogin(form -> form
						.loginPage("/admin/login")
						.loginProcessingUrl("/j_spring_security_check")
						.defaultSuccessUrl("/admin/accountInfo", true)
						.failureUrl("/admin/login?error=true")
						.usernameParameter("userName")
						.passwordParameter("password")
						.permitAll()
				)
				.logout(logout -> logout
						.logoutUrl("/admin/logout")
						.logoutSuccessUrl("/")
						.permitAll()
				);

		return http.build();
	}
}
