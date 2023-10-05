package com.ecommerce.miniproject.configuration;

import com.ecommerce.miniproject.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {
    @Autowired
    CustomUserDetailService customUserDetailService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(configurer ->
                        configurer
                                .requestMatchers("/", "shop/**","register/**","/resources/**",
                                        "/static/**","/images/**","/productImages/**","/css/**").permitAll()
                                .requestMatchers("/admin/**","/admin/userManagement/**").hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .formLogin(form ->
                        form
                                .loginPage("/login")
                                .permitAll()
                                .failureUrl("/login?error=true")
                                .defaultSuccessUrl("/", true)
                                .usernameParameter("email")
                                .passwordParameter("password")
                )
                   .logout(logout->
                           logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                   .logoutSuccessUrl("/login")
                                   .invalidateHttpSession(true)
                                   .deleteCookies("JSESSIONID")
                   )
                .exceptionHandling(configurer ->
                        configurer
                                .accessDeniedPage("/unauthorized-access")
                );
        return httpSecurity.build();
    }
@Bean
public DaoAuthenticationProvider authenticationProvider(CustomUserDetailService customUserDetailService) {
    DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
    auth.setUserDetailsService(customUserDetailService); //set the custom user details service
    auth.setPasswordEncoder(bCryptPasswordEncoder()); //set the password encoder - bcrypt
    return auth;
}


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
    }
