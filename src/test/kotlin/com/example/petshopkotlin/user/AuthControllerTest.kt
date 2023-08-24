package com.example.petshopkotlin.user

import com.example.petshopkotlin.user.model.Role
import com.example.petshopkotlin.user.model.User
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

    val validAdmin = User(
        userName = "fake_admin123@example.com",
        password = "1234567Ab$",
        role = Role.ADMIN,
    )

    val validCustomer = User(
        userName = "fake_customer@example.com",
        password = "1234567Ab$",
        role = Role.CUSTOMER,
    )

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
                    validAdmin.toJson()
                )
        )
            .andExpect(
                status().isCreated
            )
            .andReturn()
            .response
            .contentAsString

        assertEquals(content, validAdmin.userName)

        val savedUser = userRepository.findUserByUsername(validAdmin.userName)
        assertEquals(savedUser?.userName, validAdmin.userName)
        assertEquals(savedUser?.role, validAdmin.role)
    }

    @Test
    fun `given invalid userName when register then BadRequest`() {
        val invalidAdmin = User(
            userName = "example.com",
            password = "123456",
            role = Role.ADMIN,
        )

        val content = mockMvc.perform(
            post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    invalidAdmin.toJson()
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
        validCustomer
            .copy(password = encryptPassword(validCustomer.password))
            .also(userRepository::save)

        val content = mockMvc.perform(
            post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    validCustomer.toJson()
                )
        )
            .andExpect(
                status().isBadRequest
            )
            .andReturn()
            .response
            .contentAsString

        assertEquals(content, "User with ${validCustomer.userName} already exists!")
    }

    @Test
    fun `given valid credentials when login then Success along with username`() {
        validAdmin
            .copy(password = encryptPassword(validAdmin.password))
            .also(userRepository::save)

        val content = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                          {
                        "userName": "${validAdmin.userName}",
                        "password": "${validAdmin.password}"
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
    }

    private fun User.toJson(): String {
        return """
            {
                "userName": "$userName",
                "password": "$password",
                "role": "$role"
            }
        """.trimIndent()
    }
}