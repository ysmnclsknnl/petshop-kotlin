package com.example.petshopkotlin.unittests

import com.example.petshopkotlin.collection.Role
import com.example.petshopkotlin.collection.User
import com.example.petshopkotlin.controller.AuthController
import com.example.petshopkotlin.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.argThat
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.BDDMockito.given
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockitoExtension::class)
class AuthControllerUnitTests {

    @Mock
    private lateinit var service: UserService

    @InjectMocks
    private lateinit var authController: AuthController

    private lateinit var mvc: MockMvc

    @BeforeEach
    fun setUp() {
        mvc = MockMvcBuilders.standaloneSetup(authController).build()
    }
    @Test
    fun givenValidUser_whenRegister_thenSuccess() {
        val user = User(
            userName = "aaa@gmail.com",
            password = "123456",
            role = Role.CUSTOMER
        )

        // Mock the behavior of the service method using custom argument matcher
        given(service.createUser(argThat(UserMatcher(user)))).willReturn(user.userName)


        // Perform the request and verify the response
        val result = mvc.perform(
            post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\n" +
                            "    \"userName\": \"aaa@gmail.com\",\n" +
                            "    \"password\": \"123456\",\n" +
                            "    \"role\": \"CUSTOMER\"\n" +
                            "}"
                )
        )
            .andExpect(status().isCreated)
            .andReturn()
        println(result.response.errorMessage)

    }
}
