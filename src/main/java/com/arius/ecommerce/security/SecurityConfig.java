package com.arius.ecommerce.security;

import com.arius.ecommerce.config.AppConstants;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    private final JWTFilter jwtFilter;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, JWTFilter jwtFilter){
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth->auth
                .requestMatchers(AppConstants.PUBLIC_URLS).permitAll()
                .requestMatchers(AppConstants.ADMIN_URLS).hasAuthority("ADMIN")
                .requestMatchers(AppConstants.USER_URLS).hasAnyAuthority("USER","ADMIN")
                .anyRequest().authenticated())
            .httpBasic(Customizer.withDefaults())
            .exceptionHandling(exception->exception.authenticationEntryPoint((request,response,authenticationException)->{
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,"BAD REQUEST");
            }))
            .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(Customizer.withDefaults())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(daoAuthenticationProvider());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
