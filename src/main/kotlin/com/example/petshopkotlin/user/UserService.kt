package com.example.petshopkotlin.user

import com.example.petshopkotlin.user.model.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import com.example.petshopkotlin.user.model.User as ModelUser

@Service
class UserService(
    @Autowired val userRepo: UserRepository,
) : UserDetailsService {
    fun createUser(user: ModelUser): String {
        require(!userRepo.existsByUserName(user.userName)) { "User with ${user.userName} already exists!" }
        val errors = validateUser(user)
        require(errors.isBlank()) { errors }

        val password = encryptPassword(user.password)

        return userRepo.save(
            ModelUser(
                userName = user.userName,
                password = password,
                role = user.role,
            ),
        ).username
    }

    internal fun validateUser(user: ModelUser): String {

        val emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex()
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$".toRegex()

        return listOfNotNull(
            user.userName.takeIf { !it.matches(emailRegex) }
                ?.let { "Email address should consist of numbers, letters, and '.', '-', '_' symbols." },
            user.password.takeIf { !it.matches(passwordRegex) }
                ?.let { "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special symbol @$!%*?&" }
        ).joinToString(" ")
    }

    override fun loadUserByUsername(userName: String): User? = userRepo.findUserByUsername(userName)?.let {
        User(it.username, it.password, listOf(GrantedAuthority { "ROLE_${it.role.name}" }))
    }

    fun login(loginCredentials: LoginDto): Role {
        val user = userRepo.findUserByUsername(loginCredentials.userName)
            ?: throw UsernameNotFoundException("User with ${loginCredentials.userName} not found!")

        return if (BCryptPasswordEncoder().matches(loginCredentials.password, user.password)) {
            user.role
        } else {
            throw UsernameNotFoundException("Password is not correct!")
        }
    }

    private fun encryptPassword(password: String) = BCryptPasswordEncoder().encode(password)
}
