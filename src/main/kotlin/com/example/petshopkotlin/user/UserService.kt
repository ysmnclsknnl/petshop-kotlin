package com.example.petshopkotlin.user

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
    fun createUser(user: com.example.petshopkotlin.user.model.User): String {
        require(!userRepo.existsByUserName(user.userName)) {"User with ${user.userName} already exists!"}
        val errors = validateUser(user).joinToString(" ")
        require(errors.isBlank()) {errors}

        val password = BCryptPasswordEncoder().encode(user.password)

    return userRepo.save(
        com.example.petshopkotlin.user.model.User(
            userName = user.userName,
            password = password,
            role = user.role,
        ),
    ).username
    }

    private fun validateUser(user: com.example.petshopkotlin.user.model.User): List<String> {
        val emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$".toRegex()

        return listOfNotNull(
            user.userName.takeIf { !it.matches(emailRegex) }
                ?.let { "Email address should consist of numbers, letters, and '.', '-', '_' symbols." },
            user.password.takeIf { !it.matches(passwordRegex) }
                ?.let { "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special symbol @$!%*?&" }
        )
    }

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(userName: String) = userRepo.findUserByUsername(userName)?.let {
        User(it.username, it.password, listOf(GrantedAuthority { "ROLE_${it.role.name}" }))
    }
}
