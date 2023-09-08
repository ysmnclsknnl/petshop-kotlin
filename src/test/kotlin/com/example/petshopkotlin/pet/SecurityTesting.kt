package com.example.petshopkotlin.pet

import com.example.petshopkotlin.pet.model.CreatePetDTO
import com.example.petshopkotlin.pet.model.Pet
import com.example.petshopkotlin.pet.model.PetType
import com.example.petshopkotlin.security.SecurityConfig
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
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
    private lateinit var userDetailsService: UserDetailsService

    private val validCatDTO = CreatePetDTO(
        name = "Tom",
        description = "Very fast cat. Loves eating fish.",
        age = 2,
        type = PetType.CAT,
        photoLink = "https://www.foo.com",
    )

    private val validCat = validCatDTO.toPet()

    private val validDogDTO = CreatePetDTO(
        name = "Cotton",
        description = "Cute dog. Likes to play fetch",
        age = 3,
        type = PetType.DOG,
        photoLink = "https://www.hoidog.com"
    )

    private val validDog = validDogDTO.toPet()

    private val pets = listOf(
        validCat,
        validDog
    )

    private val validPetJson = validCatDTO.toJson()

    @WithAnonymousUser
    @Test
    fun `given unauthenticated user when getPets then Unauthorized`() {
        given(
            petService.getPets()
        ).willReturn(pets)

        mvc.perform(get("/api/pets"))
            .andExpect(
                status().isUnauthorized
            )
    }

    @WithMockUser(roles = ["ADMIN"])
    @Test
    fun `given Admin user when getPets then Success`() {
        given(
            petService.getPets()
        ).willReturn(pets)

        mvc.perform(get("/api/pets"))
            .andExpect(
                status().isOk
            )
    }

    @WithMockUser(roles = ["CUSTOMER"])
    @Test
    fun `given Customer user when getPets then Success`() {
        given(
            petService.getPets()
        ).willReturn(pets)

        mvc.perform(get("/api/pets"))
            .andExpect(
                status().isOk
            )
    }

    @WithMockUser(roles = ["ADMIN"])
    @Test
    fun `given Admin user when createPet then Created`() {
        given(
            petService.addPet(any())
        ).willReturn(validCat)

        mvc.perform(
            post("/api/pets")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(validPetJson)
        )
            .andExpect(
                status().isCreated
            )
    }

    @WithMockUser(roles = ["ADMIN"])
    @Test
    fun `given Admin user when createPet then BadRequest`() {
        val invalidPet = validCatDTO.copy(
            age = -1,
        )

        given(
            petService.addPet(any())
        ).willThrow(IllegalArgumentException::class.java)

        mvc.perform(
            post("/api/pets")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    invalidPet.toJson()
                )
        )
            .andExpect(
                status().isBadRequest
            )
    }

    @WithMockUser(roles = ["CUSTOMER"])
    @Test
    fun `given authenticated users with roles except Admin when createPet then Forbidden`() {
        given(
            petService.addPet(any())
        ).willReturn(validCat)

        mvc.perform(
            post("/api/pets")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(validPetJson)
        )
            .andExpect(
                status().isForbidden
            )
    }

    @WithAnonymousUser
    @Test
    fun `given unauthenticated user when createPet then Unauthorized`() {
        given(
            petService.addPet(any())
        ).willReturn(validCat)

        mvc.perform(
            post("/api/pets")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(validPetJson)
        )
            .andExpect(
                status().isUnauthorized
            )
    }

    @WithMockUser(roles = ["CUSTOMER"])
    @Test
    fun `given Customer user when adoptPet then Success`() {
        given(
            petService.adoptPet(validCat.id)
        ).willReturn("You successfully adopted ${validCat.name}!")

        mvc.perform(
            patch("/api/pets/${validCat.id}")
        )
            .andExpect(status().isOk)
    }

    @WithMockUser(roles = ["ADMIN"])
    @Test
    fun `given authenticated users with roles except customer when adoptPet then Forbidden`() {
        given(
            petService.adoptPet(validCat.id)
        ).willReturn("You successfully adopted ${validCat.name}!")

        mvc.perform(
            patch("/api/pets/${validCat.id}")
        )
            .andExpect(status().isForbidden)
    }

    @WithAnonymousUser
    @Test
    fun `given unauthenticated user when adoptPet then Unauthorized`() {
        given(
            petService.adoptPet(validCat.id)
        ).willReturn("You successfully adopted ${validCat.name}!")

        mvc.perform(
            patch("/api/pets/${validCat.id}")
        )
            .andExpect(status().isUnauthorized)
    }
}

internal fun CreatePetDTO.toJson(): String = ObjectMapper().writeValueAsString(this)