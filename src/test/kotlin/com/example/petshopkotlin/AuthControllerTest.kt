package com.example.petshopkotlin

import com.example.petshopkotlin.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertTrue
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
            mockMvc.perform(
                post("/api/auth")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\n" +
                            "    \"userName\": \"aaa@gmail.com\",\n" +
                            "    \"password\": \"123456\",\n" +
                            "    \"role\": \"CUSTOMER\"\n" +
                            "}"
            )
            )
                .andExpect(status().isCreated)

            val savedUser = userRepository.findUserByUsername("aaa@gmail.com")
            assertTrue(savedUser?.userName == "aaa@gmail.com" )
        }

    @Test
    fun givenInvalidUserName_whenRegister_thenBadRequest() {
        mockMvc.perform(
            post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"userName\": \"aaa.com\",\n" +
                        "    \"password\": \"123456\",\n" +
                        "    \"role\": \"CUSTOMER\"\n" +
                        "}"
                )
        )
            .andExpect(status().isBadRequest)




    }
}