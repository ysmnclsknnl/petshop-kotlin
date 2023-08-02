package com.example.petshopkotlin.pet

import com.example.petshopkotlin.SecurityOff
import com.example.petshopkotlin.pet.model.Pet
import com.example.petshopkotlin.pet.model.PetType
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers

@AutoConfigureMockMvc
@SpringBootTest
@Testcontainers
@ImportAutoConfiguration(SecurityOff::class)
class PetControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var petRepository: PetRepository

    @BeforeEach
    fun setUp() {
        petRepository.deleteAll()
    }

    @Test
    fun givenUnauthenticatedUser_whenGetPet_thenUnauthorized() {
        val pets = listOf(
            Pet(
                name = "Tom",
                description = "Fast cat. Loves running behind Jerry",
                age = 2,
                type = PetType.CAT,
                photoLink = "www.hoicat.com"
            ),
            Pet(
                name = "Cotton",
                description = "Cute dog. Likes to play fetch",
                age = 3,
                type = PetType.DOG,
                photoLink = "www.hoidog.com"
            ),
        ).also(petRepository::saveAll)

        mockMvc.perform(
            get("/api/pets")
        )
            .andExpect(status().isUnauthorized)
    }

    @WithMockUser(username = "user1@abc.com", password = "password", roles = ["ADMIN"])
    @Test
    fun givenAdminUser_whenGetPet_thenSuccessAndReturnsPets() {
        val pets = listOf(
            Pet(
                name = "Tom",
                description = "Fast cat. Loves running behind Jerry",
                age = 2,
                type = PetType.CAT,
                photoLink = "www.hoicat.com"
            ),
            Pet(
                name = "Cotton",
                description = "Cute dog. Likes to play fetch",
                age = 3,
                type = PetType.DOG,
                photoLink = "www.hoidog.com"
            ),
        ).also(petRepository::saveAll)

        mockMvc.perform(
            get("/api/pets")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(pets.size))
            .andExpect(jsonPath("$[*].name", Matchers.containsInAnyOrder(pets[0].name, pets[1].name)))
            .andExpect(jsonPath("$[*].age", Matchers.containsInAnyOrder(pets[0].age, pets[1].age)))
            .andExpect(
                jsonPath(
                    "$[*].description",
                    Matchers.containsInAnyOrder(pets[0].description, pets[1].description)
                )
            )
    }

    @WithMockUser(username = "user1@abc.com", password = "password", roles = ["CUSTOMER"])
    @Test
    fun givenCustomerUser_whenGetPet_thenSuccessAndReturnsPets() {
        val pets = listOf(
            Pet(
                name = "Tom",
                description = "Fast cat. Loves running behind Jerry",
                age = 2,
                type = PetType.CAT,
                photoLink = "www.hoicat.com"
            ),
            Pet(
                name = "Cotton",
                description = "Cute dog. Likes to play fetch",
                age = 3,
                type = PetType.DOG,
                photoLink = "www.hoidog.com"
            ),
        ).also(petRepository::saveAll)

        mockMvc.perform(
            get("/api/pets")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(pets.size))
            .andExpect(jsonPath("$[*].name", Matchers.containsInAnyOrder(pets[0].name, pets[1].name)))
            .andExpect(jsonPath("$[*].age", Matchers.containsInAnyOrder(pets[0].age, pets[1].age)))
            .andExpect(
                jsonPath(
                    "$[*].description",
                    Matchers.containsInAnyOrder(pets[0].description, pets[1].description)
                )
            )
    }

    @WithMockUser(username = "user1@abc.com", password = "password", roles = ["ADMIN"])
    @Test
    fun givenAdminUserWithValidPetData_whenCreatePet_thenSuccess() {
        mockMvc.perform(
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
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Amsterdam"))
            .andExpect(jsonPath("$.description").value("Lovely and strong dog. Really good football player!"))
            .andExpect(jsonPath("$.age").value(0))
            .andExpect(jsonPath("$.type").value("DOG"))
            .andExpect(jsonPath("$.photoLink").value("wwww.image.com"))
    }

    @WithMockUser(username = "user1@abc.com", password = "password", roles = ["ADMIN"])
    @Test
    fun givenInvalidPetData_whenCreatePet_thenBadRequest() {
        val result = mockMvc.perform(
            post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "name": "As",
                    "description": "Lovely !",
                    "age": -1,
                    "type": "DOG",
                    "photoLink": "wwww.image.com"
                }
                """.trimIndent()
                )
        )
            .andExpect(status().isBadRequest)
            .andReturn()

        val content = result.response.errorMessage
        assertEquals(
            content,
            "Name must be at least 3 characters. Description must be at least 15 characters. Age must be at least 0."
        )
    }

    @WithMockUser(username = "user1@abc.com", password = "password", roles = ["CUSTOMER"])
    @Test
    fun givenOtherRolesExceptAdmin_whenCreatePet_thenForbidden() {
        mockMvc.perform(
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

    @Test
    fun givenUnauthenticatedUser_whenCreatePet_thenUnauthorized() {
        mockMvc.perform(
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

    @WithMockUser(username = "user1@abc.com", password = "password", roles = ["CUSTOMER"])
    @Test
    fun givenCustomerUser_whenAdoptPet_thenSuccess() {
        val pet = Pet(
            name = "Tom",
            description = "Fast cat. Loves running behind Jerry",
            age = 2,
            type = PetType.CAT,
            adopted = false,
            photoLink = "www.hoicat.com"
        ).also(petRepository::save)

        mockMvc.perform(
            patch("/api/pets/${pet.id}")
        )
            .andExpect(status().isOk)
    }

    @WithMockUser(username = "user1@abc.com", password = "password", roles = ["CUSTOMER"])
    @Test
    fun givenAdoptedPet_whenAdoptPet_thenBadRequest() {
        val pet = Pet(
            name = "Tom",
            description = "Fast cat. Loves running behind Jerry",
            age = 2,
            type = PetType.CAT,
            adopted = true,
            photoLink = "www.hoicat.com"
        ).also(petRepository::save)

        mockMvc.perform(
            patch("/api/pets/${pet.id}")
        )
            .andExpect(status().isBadRequest)
    }

    @WithMockUser(username = "user1@abc.com", password = "password", roles = ["ADMIN"])
    @Test
    fun givenUsersWithRolesExceptCustomer_whenAdoptPet_thenForbidden() {
        val pet = Pet(
            name = "Tom",
            description = "Fast cat. Loves running behind Jerry",
            age = 2,
            type = PetType.CAT,
            adopted = false,
            photoLink = "www.hoicat.com"
        ).also(petRepository::save)

        mockMvc.perform(
            patch("/api/pets/${pet.id}")
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun givenUnauthenticatedUser_whenAdoptPet_thenUnauthorized() {
        val pet = Pet(
            name = "Tom",
            description = "Fast cat. Loves running behind Jerry",
            age = 2,
            type = PetType.CAT,
            adopted = false,
            photoLink = "www.hoicat.com"
        ).also(petRepository::save)

        mockMvc.perform(
            patch("/api/pets/${pet.id}")
        )
            .andExpect(status().isUnauthorized)
    }
}
