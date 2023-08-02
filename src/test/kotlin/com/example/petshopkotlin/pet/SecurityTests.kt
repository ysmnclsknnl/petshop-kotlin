package com.example.petshopkotlin.pet

import com.example.petshopkotlin.pet.model.Pet
import com.example.petshopkotlin.pet.model.PetType
import com.example.petshopkotlin.security.SecurityConfig
import com.example.petshopkotlin.user.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PetController::class)
@ImportAutoConfiguration(SecurityConfig::class)
class SecurityTests {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var petService: PetService

    @MockBean
    private lateinit var userService: UserService
    @Test
    fun givenUnauthenticatedUser_whenGetPet_thenUnauthorized() {
        val pets = listOf(Pet(
            name = "Tom",
            description = "Very fast cat. Loves eating fish.",
            age = 2,
            type = PetType.CAT,
            adopted = true,
            photoLink = "www.foo.com",
        ),
            Pet(
                name = "Dog",
                description = "Very fast dog. Loves eating meat.",
                age = 2,
                type = PetType.DOG,
                adopted = true,
                photoLink = "www.foo.com",
            ),
            )

        given(petService.getPets()).willReturn(pets)

        mvc.perform(get("/api/pets"))
            .andExpect(status().isUnauthorized)
    }

    @WithMockUser(roles = ["ADMIN"])
    @Test
    fun givenAdminUser_whenGetPet_thenSuccess() {
        val pets = listOf(Pet(
            name = "Tom",
            description = "Very fast cat. Loves eating fish.",
            age = 2,
            type = PetType.CAT,
            adopted = true,
            photoLink = "www.foo.com",
        ),
            Pet(
                name = "Dog",
                description = "Very fast dog. Loves eating meat.",
                age = 2,
                type = PetType.DOG,
                adopted = true,
                photoLink = "www.foo.com",
            ),
        )

        given(petService.getPets()).willReturn(pets)

        mvc.perform(get("/api/pets"))
            .andExpect(status().isOk)
    }
    @WithMockUser(roles = ["CUSTOMER"])
    @Test
    fun givenCustomerUser_whenGetPet_thenSuccess() {
        val pets = listOf(Pet(
            name = "Tom",
            description = "Very fast cat. Loves eating fish.",
            age = 2,
            type = PetType.CAT,
            adopted = true,
            photoLink = "www.foo.com",
        ),
            Pet(
                name = "Dog",
                description = "Very fast dog. Loves eating meat.",
                age = 2,
                type = PetType.DOG,
                adopted = true,
                photoLink = "www.foo.com",
            ),
        )

        given(petService.getPets()).willReturn(pets)

        mvc.perform(get("/api/pets"))
            .andExpect(status().isOk)
    }

    @WithMockUser(roles = ["ADMIN"])
    @Test
    fun givenAdminUser_WhenCreatePet_thenCreated() {
        val pet = Pet(
            name = "Tom",
            description = "Very fast cat. Loves eating fish.",
            age = 2,
            type = PetType.CAT,
            adopted = true,
            photoLink = "www.foo.com",
        )

        given(petService.addPet(pet)).willReturn(pet)

        mvc.perform(
            post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(pet)
                )
        )
            .andExpect(status().isCreated)
    }

    @WithMockUser(roles = ["CUSTOMER"])
    @Test
    fun givenOtherRolesExceptAdmin_whenCreatePet_thenForbidden() {
        mvc.perform(
            post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "name": "Amsterdam",
                    "description": "Lovely and strong dog. Really good football player!",
                    "age": 0,
                    "type": "DOG",
                    "photoLink": "wwww.image.com"
                }
                """.trimIndent()
                )
        )
            .andExpect(status().isForbidden)
    }

    @WithAnonymousUser
    @Test
    fun givenUnauthenticatedUser_whenCreatePet_thenUnauthorized() {
        mvc.perform(
            post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "name": "Amsterdam",
                    "description": "Lovely and strong dog. Really good football player!",
                    "age": 0,
                    "type": "DOG",
                    "photoLink": "wwww.image.com"
                }
                """.trimIndent()
                )
        )
            .andExpect(status().isUnauthorized)
    }
}
