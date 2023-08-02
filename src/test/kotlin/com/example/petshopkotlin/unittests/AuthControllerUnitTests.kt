package com.example.petshopkotlin.unittests

import com.example.petshopkotlin.user.model.Role
import com.example.petshopkotlin.user.model.User
import com.example.petshopkotlin.user.AuthController
import com.example.petshopkotlin.user.UserService
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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
    fun givenValidUser_whenRegister_thenCreated() {
        val user = User(
            id = ObjectId("64c3bd122e034e4dcc32ebf0"),
            userName = "aaa@gmail.com",
            password = "123456",
            role = Role.CUSTOMER
        )


        given(service.createUser(user)).willReturn(user.userName)

        val result = mvc.perform(
            post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                """
                            { 
                              "id" : "64c3bd122e034e4dcc32ebf0",
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

         assertEquals(content, user.userName )

    }

    @Test
    fun givenInValidUser_whenRegister_thenBadRequest() {
        val user = User(
            id = ObjectId("64c3bd122e034e4dcc32ebf0"),
            userName = "com",
            password = "123456",
            role = Role.CUSTOMER
        )

        val errorMessage = "Email address should consist of numbers, letters and '.', '-', '_' symbols"

        given(service.createUser(user)).willThrow(IllegalArgumentException(errorMessage))

        val result = mvc.perform(
            post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                            { 
                              "id" : "64c3bd122e034e4dcc32ebf0",
                              "userName": "com",
                               "password": "123456",
                               "role": "CUSTOMER"
                            }
                            """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
            .andReturn()
        val content = result.response.errorMessage

        assertEquals(content, errorMessage )

    }
}
