package com.example.springsecurity.config;

import com.example.springsecurity.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;

import static com.example.springsecurity.entity.Permission.*;
import static com.example.springsecurity.entity.Role.ADMIN;
import static com.example.springsecurity.entity.Role.COMPANY;
import static com.example.springsecurity.entity.Role.USER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@CrossOrigin
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final  AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    //@Bean : au demarrage spring execute cette methode.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/auth/**","/user/forgot-password","/user/set-password","/user/checkCompanyName","/user/checkUserEmail","/job/getJobs","/job/nbJobs","/job/fetchJobsBySkills","/job/serachJobs","/job/findJobById","/job/getSimilarJobs", "/job/companyJobs","/job/companyOpenJobs","/user/getCompanies","/user/nbUsers","/user/userRole","/job/getTopCompanies")
                    .permitAll()
                //.requestMatchers("/management/**").hasAnyRole(ADMIN.name(), MANAGER.name())
                .requestMatchers("/management/**","/user/**","/skills/getUserSkills","/skills/getSkills","/experience/getExperienceById","/education/educationsByUserId").hasAnyAuthority("ADMIN", "COMPANY","USER")
                .requestMatchers("/skills/**","/experience/**","/education/**","/cv/**","/savedJobs/**","/job/applyJob","/job/getUserAppliedJobs","/jobAlert/**").hasAnyAuthority("USER")
                .requestMatchers("/admin/**","/user/deleteUser","/job/deleteJobByAdmin","/job/getJobsByMonth","/job/getJobsByJobType").hasAuthority("ADMIN")
                .requestMatchers("/job/**").hasAuthority("COMPANY")

                .anyRequest()
                .authenticated()
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());
                //.and()
                //.oauth2Login();


        return  http.build();

    }
}
