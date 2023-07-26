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
) : UserDetailsService {

//     fun createUser(user: com.example.petshopkotlin.collection.User) {
//        if (!userRepo.existsByUserName(user.username)) {
//           passwordEncoder.encode(user.password).let { userRepo.save(com.example.petshopkotlin.collection.User(user.username, it, user.role)) }
//            User(user.username, password, listOf(GrantedAuthority { "ROLE_${it.role.name}" }))
//
//        } else {
//            throw IllegalArgumentException("User exists with email" + user.username)
//        }
//    }

    fun createUser(user: com.example.petshopkotlin.collection.User): com.example.petshopkotlin.collection.User {
        userRepo.existsByUserName(user.userName)
        val password = BCryptPasswordEncoder().encode(user.password)
    return userRepo.save(
        com.example.petshopkotlin.collection.User(
        userName = user.userName,
        password = password,
        role = user.role,
        ),
    )
    }

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(userName: String) = userRepo.findUserByUsername(userName)?.let {
        User(it.username, it.password, listOf(GrantedAuthority { "ROLE_${it.role.name}" }))
    }
}
