
package com.example.petshopkotlin.collection

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Document(collection = "user")
data class User(
    @Id
    val userName: String,
    val password: String,
    val role: Role,
) : UserDetails {
    override fun getAuthorities() = listOf(SimpleGrantedAuthority("ROLE_$role"))
    override fun getPassword() = password

    override fun getUsername() = userName

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true
}

