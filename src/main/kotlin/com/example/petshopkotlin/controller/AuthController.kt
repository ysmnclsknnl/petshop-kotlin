package com.example.petshopkotlin.controller

import com.example.petshopkotlin.collection.User
import com.example.petshopkotlin.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/auth")
class AuthController(@Autowired val userService: UserService) {
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
            ResponseEntity.ok(userService.loadUserByUsername(username))
        } catch (e: AuthenticationException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect email or password.", e)
        }
    }
}
