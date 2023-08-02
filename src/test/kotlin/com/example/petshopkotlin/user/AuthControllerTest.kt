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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers

@AutoConfigureMockMvc
@SpringBootTest
@Testcontainers
class AuthControllerTest {
    @Autowired
    lateinit var mockMvc : MockMvc

    @Autowired
    lateinit var userRepository: UserRepository


    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
    }

        @Test
        fun givenValidUser_whenRegister_thenSuccess() {
          val result = mockMvc.perform(
                post("/api/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                            {
                              "userName": "aaa@gmail.com",
                               "password": "123456",
                               "role": "CUSTOMER"
                            }
                            """.trimIndent()
                    )
          )
                .andExpect(status().isCreated)
              .andReturn()

            val content = result.response.contentAsString
            assertEquals(content,"aaa@gmail.com" )

            val savedUser = userRepository.findUserByUsername("aaa@gmail.com")
            assertEquals(savedUser?.userName ,  "aaa@gmail.com" )
        }

    @Test
    fun givenInvalidUserName_whenRegister_thenBadRequest() {
        val result = mockMvc.perform(
            post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                            {
                              "userName": "aagmail.com",
                               "password": "123456",
                               "role": "CUSTOMER"
                            }
                            """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
            .andReturn()

        val content = result.response.errorMessage

        assertEquals(content, "Email address should consist of numbers, letters and '.', '-', '_' symbols")
    }

    @Test
    fun givenExistingUserName_whenRegister_thenBadRequest() {
        val user = User(
            userName = "aaa@gmail.com",
            password = "123456",
            role = Role.CUSTOMER,
        ).also(userRepository::save)

        val result = mockMvc.perform(
            post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                            {
                              "userName": "aaa@gmail.com",
                               "password": "123456",
                               "role": "CUSTOMER"
                            }
                            """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
            .andReturn()

        val content = result.response.errorMessage

        assertEquals(content, "User with ${user.userName} already exists!")
    }
}