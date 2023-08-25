package com.example.petshopkotlin.user

import com.example.petshopkotlin.user.model.Role
import com.example.petshopkotlin.user.model.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
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
    fun `validateUser should return null when user is valid`() {
        val exception = validateUser(validUser)

        assertEquals(null, exception)
    }

    @Test
    fun `validateUser should throw User Validation Exception when username is invalid`() {
        val userWithInvalidName = validUser.copy(userName = "foo")

        val exception = assertThrows(
            UserValidationException::class.java
        ) {
            validateUser(userWithInvalidName)?.let { throw it }
        }

        assertEquals(
            "Email address should consist of numbers, letters, and '.', '-', '_' symbols.",
            exception.message
        )
    }

    @Test
    fun `validateUser should throw User Validation Exception when password is invalid`() {
        val userWithInvalidPassword = validUser.copy(password = "1234567")

        val exception = assertThrows(
            UserValidationException::class.java
        ) {
            validateUser(userWithInvalidPassword)?.let { throw it }
        }

        assertEquals(
            "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special symbol @$!%*?&",
            exception.message
        )
    }
}