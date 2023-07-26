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
    fun createUser(user: com.example.petshopkotlin.collection.User): com.example.petshopkotlin.collection.User {
         if (userRepo.existsByUserName(user.userName)) {
             throw IllegalArgumentException("User with ${user.userName} already exists!")
         }

        val errors = validateUser(user).joinToString(" ")
        if (errors.isNotBlank()) {
            throw IllegalArgumentException(errors)
        }

        val password = BCryptPasswordEncoder().encode(user.password)

    return userRepo.save(
        com.example.petshopkotlin.collection.User(
        userName = user.userName,
        password = password,
        role = user.role,
        ),
    )
    }

    private fun validateUser(user: com.example.petshopkotlin.collection.User) = listOfNotNull(
        if (
            !user.userName.matches("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex())
            ) {
                "Email address should consist of numbers, letters and '.', '-', '_' symbols"
            } else {
                null
            },
        if (
            !user.password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.])[A-Za-z\\d@$!%*?&]{8,}$".toRegex())
            ) {
            "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special symbol (@$!%*?&.)."
            } else {
                null
            },
    )

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(userName: String) = userRepo.findUserByUsername(userName)?.let {
        User(it.username, it.password, listOf(GrantedAuthority { "ROLE_${it.role.name}" }))
    }
}
