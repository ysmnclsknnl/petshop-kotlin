package com.example.petshopkotlin.user

import com.example.petshopkotlin.user.model.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val userService: UserService) {
    @PostMapping
    fun register(@RequestBody user: User) = ResponseEntity
        .status(
            HttpStatus.CREATED
        ).body(
            userService.createUser(user)
        )

    @PostMapping("/login")
    fun login(@RequestBody loginCredentials: LoginDto) = userService.login(loginCredentials)
}
