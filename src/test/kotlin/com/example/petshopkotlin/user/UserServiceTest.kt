package com.example.petshopkotlin.user

import com.example.petshopkotlin.user.model.Role
import com.example.petshopkotlin.user.model.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private val userService by lazy { UserService(userRepository) }

    private val validUser = User(
        userName = "foo@abc.com",
        password = "1234567Ab%",
        role = Role.CUSTOMER
    )

    @Test
    fun `validateUser should return empty string when user is valid`() {
        val validationResult = userService.validateUser(validUser)

        assertEquals("", validationResult)
    }

    @Test
    fun `validateUser should return an error message when username is invalid`() {
        val userWithInvalidName = validUser.copy(userName = "foo")

        val validationResult = userService.validateUser(userWithInvalidName)

        assertEquals(
            "Email address should consist of numbers, letters, and '.', '-', '_' symbols.",
            validationResult
        )
    }

    @Test
    fun `validateUser should return an error message when password is invalid`() {
        val userWithInvalidPassword = validUser.copy(password = "1234567")

        val validationResult = userService.validateUser(userWithInvalidPassword)

        assertEquals(
            "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special symbol @$!%*?&",
            validationResult
        )
    }
}