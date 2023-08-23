package com.example.petshopkotlin.user

import com.example.petshopkotlin.user.model.Role
import com.example.petshopkotlin.user.model.User
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers

@AutoConfigureMockMvc
@SpringBootTest
@Testcontainers
class AuthControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
    }

    @Test
    fun `given valid user when register then Success along with userName`() {
        val content = mockMvc.perform(
            post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                         {
                              "userName": "fake_admin123@example.com",
                               "password": "1234567Ab$",
                               "role": "ADMIN"
                            }
                            """.trimIndent()
                )
        )
            .andExpect(
                status().isCreated
            )
            .andReturn()
            .response
            .contentAsString

        assertEquals(content, "fake_admin123@example.com")

        val savedUser = userRepository.findUserByUsername("fake_admin123@example.com")
        assertEquals(savedUser?.userName, "fake_admin123@example.com")
    }

    @Test
    fun `given invalid userName when register then BadRequest`() {
        val content = mockMvc.perform(
            post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                            {
                              "userName": "example.com",
                               "password": "123456",
                               "role": "CUSTOMER"
                            }
                            """.trimIndent()
                )
        )
            .andExpect(
                status().isBadRequest
            )
            .andReturn()
            .response
            .contentAsString

        val errors = listOf(
            "Email address should consist of numbers, letters, and '.', '-', '_' symbols.",
            "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special symbol @$!%*?&"
        ).joinToString(" ")

        assertEquals(content, errors)
    }

    @Test
    fun `given an existing username when register then BadRequest`() {
        val user = User(
            userName = "fake_user123@example.com",
            password = "1234567Ab$",
            role = Role.ADMIN,
        ).also(userRepository::save)

        val content = mockMvc.perform(
            post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                            {
                              "userName": "fake_user123@example.com",
                               "password": "1234567Ab$",
                               "role": "ADMIN"
                            }
                            """.trimIndent()
                )
        )
            .andExpect(
                status().isBadRequest
            )
            .andReturn()
            .response
            .contentAsString

        assertEquals(content, "User with ${user.userName} already exists!")
    }

    @Test
    fun `given valid credentials when login then Success along with username`() {
        val user = User(
            userName = "adminuser2@foo.com",
            password = "12345BCd$",
            role = Role.ADMIN,
        ).also(userRepository::save)

        println(userRepository.findUserByUsername(user.userName))
        val loginDto = LoginDto(
            userName = user.userName,
            password = user.password,
        )
        val content = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                          {
                        "userName": "adminuser2@foo.com",
                        "password": "12345BCd$"
                    }
                    """.trimIndent()

                )
        )
            .andExpect(
                status().isOk
            )
            .andReturn()
            .response
            .contentAsString

        assertEquals(content, user.role)
    }
}