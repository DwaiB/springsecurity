package com.github.ssecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.github.ssecurity.repository.UserRepository;

@Configuration
public class ApplicationConfiguration {
	@Autowired
	private UserRepository repository;
	
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	UserDetailsService userDetailsService() {
//		UserDetails normalUser = User.withUsername("user").password(passwordEncoder().encode("pass")).roles("user").build();
//		
//		UserDetails adminUser = User.withUsername("admin").password(passwordEncoder().encode("apass")).roles("admin").build();
//		
//		InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
//		userDetailsManager.createUser(adminUser);
//		userDetailsManager.createUser(normalUser);
		
		return username -> repository.findByName(username).get(); 
	}
	
//	@Bean
//	SecurityFilterChain filterChain(HttpSecurity security)throws Exception{
//		security.authorizeHttpRequests((authorizeHttpRequests)-> authorizeHttpRequests.requestMatchers("/normal","/")
//		.hasRole("user")
//		.requestMatchers("/admin","/**")
//		.hasRole("admin")
//		.requestMatchers("/**")
//		.permitAll()
//				).securityContext(context -> new RequestAttributeSecurityContextRepository()).formLogin(Customizer.withDefaults());
//		
//		return security.build();
//	}
	@Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
	
}
