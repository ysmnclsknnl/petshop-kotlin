package com.example.petshopkotlin.pet

import com.example.petshopkotlin.SecurityOff
import com.example.petshopkotlin.pet.model.Pet
import com.example.petshopkotlin.pet.model.PetType
import org.bson.types.ObjectId
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@AutoConfigureMockMvc
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

    private val validCat = Pet(
        name = "Tom",
        description = "Very fast cat. Loves eating fish.",
        age = 2,
        type = PetType.CAT,
        adopted = false,
        photoLink = "https://www.foo.com",
    )

    private val validDog = Pet(
        name = "Cotton",
        description = "Cute dog. Likes to play fetch",
        age = 3,
        type = PetType.DOG,
        photoLink = "https://www.hoidog.com"
    )

    @Test
    fun getPets() {
        val pets = listOf(
            validCat,
            validDog
        ).also(petRepository::saveAll)

        mockMvc.perform(
            get("/api/pets")
        )
            .andExpect(
                status().isOk
            )
            .andExpect(
                jsonPath(
                    "$.length()", Matchers.equalTo(pets.size)
                )
            )
            .andExpect(
                jsonPath(
                    "$[*].name", Matchers.containsInAnyOrder(pets[0].name, pets[1].name)
                )
            )
            .andExpect(
                jsonPath(
                    "$[*].age", Matchers.containsInAnyOrder(pets[0].age, pets[1].age)
                )
            )
            .andExpect(
                jsonPath(
                    "$[*].description", Matchers.containsInAnyOrder(pets[0].description, pets[1].description)
                )
            )
            .andExpect(
                jsonPath(
                    "$[*].type", Matchers.containsInAnyOrder(pets[0].type.toString(), pets[1].type.toString())
                )
            )
            .andExpect(
                jsonPath(
                    "$[*].photoLink", Matchers.containsInAnyOrder(pets[0].photoLink, pets[1].photoLink)
                )
            )
    }

    @Test
    fun `createPet should return a pet along with status created when pet is valid`() {
        mockMvc.perform(
            post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    validCat.toJson()
                )
        )
            .andExpect(
                status().isCreated
            )
            .andExpect(
                jsonPath(
                    "$.name", Matchers.equalTo(validCat.name)
                )
            )
            .andExpect(
                jsonPath(
                    "$.description", Matchers.equalTo(validCat.description)
                )
            )
            .andExpect(
                jsonPath(
                    "$.age", Matchers.equalTo(validCat.age)
                )
            )
            .andExpect(
                jsonPath(
                    "$.type", Matchers.equalTo(validCat.type.toString())
                )
            )
            .andExpect(
                jsonPath(
                    "$.photoLink", Matchers.equalTo(validCat.photoLink)
                )
            )
    }

    @Test
    fun `createPet should return status BadRequest when pet is invalid `() {
        val invalidDog = Pet(
            name = "As",
            description = "Lovely !",
            age = -1,
            type = PetType.DOG,
            photoLink = "www.image.com"
        )

        val content = mockMvc.perform(
            post("/api/pets")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    invalidDog.toJson()
                )
        )
            .andExpect(
                status().isBadRequest
            )
            .andReturn()
            .response
            .contentAsString

        assertEquals(
            content,
            "Name must be at least 3 characters. Description must be at least 15 characters. Age must be at least 0. Image link should should start with http or https and not contain spaces."
        )
    }

    @Test
    fun `adoptPet should return a success message along with status ok, when pet id is valid and pet is not adopted`() {
        val pet = petRepository.save(validCat.copy(adopted = false))

        val content = mockMvc.perform(
            patch(
                "/api/pets/${pet.id}"
            )
        )
            .andExpect(
                status().isOk
            )
            .andReturn()
            .response
            .contentAsString

        assertEquals(content, "You successfully adopted ${pet.name}!")
    }

    @Test
    fun `adoptPet should return an error message along with status BadRequest, when pet is adopted`() {
        val adoptedCat = validCat
            .copy(adopted = true)
            .also(petRepository::save)

        val content = mockMvc.perform(
            patch("/api/pets/${adoptedCat.id}")
        )
            .andExpect(status().isBadRequest)
            .andReturn()
            .response
            .contentAsString

        assertEquals(content, "Pet with ID: ${adoptedCat.id} is already adopted")
    }

    @Test
    fun `adoptPet should return an error message along with status BadRequest, when pet doesn't exist`() {
        val id = ObjectId()

        val content = mockMvc.perform(
            patch("/api/pets/${id}")
        )
            .andExpect(
                status().isBadRequest
            )
            .andReturn()
            .response
            .contentAsString

        assertEquals(content, "Pet with ID: $id doesn't exist")
    }
}
