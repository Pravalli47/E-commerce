package com.jtspringproject.JtSpringProject.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.jtspringproject.JtSpringProject.services.CustomUserDetailService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final CustomUserDetailService customUserDetailService;

    public SecurityConfiguration(CustomUserDetailService customUserDetailService) {
        this.customUserDetailService = customUserDetailService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf()
            .and()
            .authorizeRequests()

            //  Public URLs
            .antMatchers("/", "/login", "/register", "/newuserregister").permitAll()
            .antMatchers("/css/**", "/js/**", "/images/**").permitAll()

            //  Admin URLs
            .antMatchers("/admin/**").hasRole("ADMIN")

            // User URLs (ONLY user paths)
            .antMatchers("/user/**", "/profileDisplay", "/buy").hasRole("NORMAL")

            // everything else requires authentication
            .anyRequest().authenticated()

            .and()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/userloginvalidate")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()

            .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll();

        return http.build();
    }
}
