package com.example.petshopkotlin.service

import com.example.petshopkotlin.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    @Autowired val userRepo: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) : UserDetailsService {
    fun createUser(user: User) {
        if (!userRepo.existsByUserName(user.username)) {
           passwordEncoder.encode(user.password).let{userRepo.save(User(user.username, it, user.role))}

        } else {
            throw IllegalArgumentException("User exists with email" + user.username)
        }
    }
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(userName: String) = userRepo.findUserByUsername(userName)?.let {
        User(it.userName, it.password, listOf(GrantedAuthority { "ROLE_${it.role.name}" }))
    }
}
