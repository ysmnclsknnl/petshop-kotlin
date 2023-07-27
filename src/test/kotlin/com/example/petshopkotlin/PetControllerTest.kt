package com.example.petshopkotlin

import com.example.petshopkotlin.collection.Pet
import com.example.petshopkotlin.collection.PetType
import com.example.petshopkotlin.repository.PetRepository
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
@Testcontainers
class PetControllerTest {
    @Autowired
    lateinit var mockMvc : MockMvc

    @Autowired
    lateinit var petRepository: PetRepository


    @BeforeEach
    fun setUp() {
        petRepository.deleteAll()
    }

    @WithMockUser(username = "user1@abc.com", password = "password", roles = ["ADMIN"])
        @Test
        fun givenAdminUser_whenCreatePet_thenSuccess() {
            mockMvc.perform(
                post("/api/pets")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\n" +
                            "    \"name\": \"hoiii\",\n" +
                            "    \"description\": \"lorem ipsum amed akdkdnddndsfsdfsdfsdfsdfdfdfdfg\",\n" +
                            "    \"age\": 0,\n" +
                            "    \"type\": \"CAT\",\n" +
                            "    \"photoLink\": \"wwww.image.com\"\n" +
                            "}"
            )
            )
                .andExpect(status().isOk)
        }

    @WithMockUser(username = "user1@abc.com", password = "password", roles = ["CUSTOMER"])
    @Test
    fun givenOtherRolesExceptAdmin_whenCreatePet_thenUnauthorized() {
        mockMvc.perform(
            post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"name\": \"hoiii\",\n" +
                        "    \"description\": \"lorem ipsum amed akdkdnddndsfsdfsdfsdfsdfdfdfdfg\",\n" +
                        "    \"age\": 0,\n" +
                        "    \"type\": \"CAT\",\n" +
                        "    \"photoLink\": \"wwww.image.com\"\n" +
                        "}"
                )
        )
            .andExpect(status().isForbidden)
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
        )

        val savedPets = petRepository.saveAll(pets)

        mockMvc.perform(
            get("/api/pets"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(pets.size))
            .andExpect(jsonPath("$[*].name", Matchers.containsInAnyOrder(pets[0].name, pets[1].name)))
            .andExpect(jsonPath("$[*].age", Matchers.containsInAnyOrder(pets[0].age, pets[1].age)))
            .andExpect(jsonPath("$[*].description", Matchers.containsInAnyOrder(pets[0].description, pets[1].description)))
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
        )

        val savedPets = petRepository.saveAll(pets).sortedBy { it.id }

        mockMvc.perform(
            get("/api/pets"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(pets.size))
            .andExpect(jsonPath("$[*].name", Matchers.containsInAnyOrder(pets[0].name, pets[1].name)))
            .andExpect(jsonPath("$[*].age", Matchers.containsInAnyOrder(pets[0].age, pets[1].age)))
            .andExpect(jsonPath("$[*].description", Matchers.containsInAnyOrder(pets[0].description, pets[1].description)))
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
            patch("/api/pets/${pet.id}"))
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
            patch("/api/pets/${pet.id}"))
            .andExpect(status().isBadRequest)
    }

    @WithMockUser(username = "user1@abc.com", password = "password", roles = ["ADMIN"])
    @Test
    fun givenUsersExceptCustomer_whenAdoptPet_thenForbidden() {
        val pet = Pet(
            name = "Tom",
            description = "Fast cat. Loves running behind Jerry",
            age = 2,
            type = PetType.CAT,
            adopted = false,
            photoLink = "www.hoicat.com"
        ).also(petRepository::save)

        mockMvc.perform(
            patch("/api/pets/${pet.id}"))
            .andExpect(status().isForbidden)
    }

}