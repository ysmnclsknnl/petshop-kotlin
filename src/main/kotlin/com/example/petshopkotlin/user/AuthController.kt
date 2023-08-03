package com.example.petshopkotlin.user

import com.example.petshopkotlin.user.model.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/auth")
class AuthController(private val userService: UserService) {
    @PostMapping
    fun register(@RequestBody user: User): ResponseEntity<String> {
        return try {
            ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user))
        } catch (e: AuthenticationException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect email or password.", e)
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody username: String): ResponseEntity<org.springframework.security.core.userdetails.User> {
        return try {
            val user = userService.loadUserByUsername(username)
            ResponseEntity.ok(user)
        }
        catch(e: UsernameNotFoundException)
        {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect email or password.", e)
        }
    }
}
