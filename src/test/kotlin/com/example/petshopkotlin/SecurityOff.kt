package com.example.petshopkotlin

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@Primary
@EnableWebSecurity
internal class SecurityOff {

    @Bean
    fun unsecureFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf {
                it.disable()
            }
            .authorizeHttpRequests {
                it.anyRequest().permitAll()
            }
            .sessionManagement { SessionCreationPolicy.STATELESS }
        return http.build()
    }

}
