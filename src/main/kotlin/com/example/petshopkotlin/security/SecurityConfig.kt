package com.example.petshopkotlin.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true)
class SecurityConfig(val userDetailsService: UserDetailsService) {
    @Bean
    @Throws(Exception::class)
    fun customAuthenticationManager(http: HttpSecurity): AuthenticationManager {
        val authenticationManagerBuilder: AuthenticationManagerBuilder = http.getSharedObject(
            AuthenticationManagerBuilder::class.java,
        )
        authenticationManagerBuilder.userDetailsService(userDetailsService)
            .passwordEncoder(bCryptPasswordEncoder())
        return authenticationManagerBuilder.build()
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf()
            .disable()
            .authorizeRequests()
            .and()
            .httpBasic()
            .and()
            .authorizeRequests()
            .anyRequest()
            .permitAll()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        return http.build()
    }
}
