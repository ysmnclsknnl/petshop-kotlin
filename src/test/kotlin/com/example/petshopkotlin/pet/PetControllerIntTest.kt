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
    lateinit var mockMvc : MockMvc

    @Autowired
    lateinit var petRepository: PetRepository

    @BeforeEach
    fun setUp() {
        petRepository.deleteAll()
    }

    @Test
    fun getPets() {
        val pets = listOf(
            Pet(
                name = "Tom",
                description = "Fast cat. Loves running behind Jerry",
                age = 2,
                type = PetType.CAT,
                photoLink = "https://www.hoicat.com"
            ),
            Pet(
                name = "Cotton",
                description = "Cute dog. Likes to play fetch",
                age = 3,
                type = PetType.DOG,
                photoLink = "https://www.hoidog.com"
            ),
        ).also(petRepository::saveAll)

        mockMvc.perform(
            get("/api/pets"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(pets.size))
            .andExpect(jsonPath("$[*].name", Matchers.containsInAnyOrder(pets[0].name, pets[1].name)))
            .andExpect(jsonPath("$[*].age", Matchers.containsInAnyOrder(pets[0].age, pets[1].age)))
            .andExpect(jsonPath("$[*].description", Matchers.containsInAnyOrder(pets[0].description, pets[1].description)))
            .andExpect(jsonPath("$[*].type", Matchers.containsInAnyOrder(pets[0].type.toString(), pets[1].type.toString())))
            .andExpect(jsonPath("$[*].photoLink", Matchers.containsInAnyOrder(pets[0].photoLink, pets[1].photoLink)))
    }

        @Test
        fun `createPet should return a pet along with status created when pet is valid`() {
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
                    "photoLink": "https://www.hoidog.com"
                }
                """.trimIndent()
                )
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Amsterdam"))
            .andExpect(jsonPath("$.description").value("Lovely and strong dog. Really good football player!"))
            .andExpect(jsonPath("$.age").value(0))
            .andExpect(jsonPath("$.type").value("DOG"))
            .andExpect(jsonPath("$.photoLink").value("https://www.hoidog.com"))
        }

    @Test
    fun `createPet should return status BadRequest when pet is invalid `() {
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
        assertEquals(content,"Name must be at least 3 characters. Description must be at least 15 characters. Age must be at least 0. Image link should should start with http or https and not contain spaces.")
    }

    @Test
    fun `adoptPet should return a success message along with status ok, when pet id is valid and pet is not adopted`() {
        val pet = Pet(
            name = "Tom",
            description = "Fast cat. Loves running behind Jerry",
            age = 2,
            type = PetType.CAT,
            adopted = false,
            photoLink = "https://www.hoicat.com"
        ).also(petRepository::save)

        val content = mockMvc.perform(
            patch("/api/pets/${pet.id}"))
            .andExpect(status().isOk)
            .andReturn()
            .response
            .contentAsString

        assertEquals(content, "You successfully adopted ${pet.name}!")
    }

    @Test
    fun `adoptPet should return an error message along with status BadRequest, when pet is adopted`() {
        val pet = Pet(
            name = "Tom",
            description = "Fast cat. Loves running behind Jerry",
            age = 2,
            type = PetType.CAT,
            adopted = true,
            photoLink = "https://www.hoicat.com"
        ).also(petRepository::save)

       val content =  mockMvc.perform(
            patch("/api/pets/${pet.id}"))
            .andExpect(status().isBadRequest)
           .andReturn().response.errorMessage

        assertEquals(content, "Pet with ID: ${pet.id} is already adopted")
    }

    @Test
    fun `adoptPet should return an error message along with status BadRequest, when pet doesn't exist`() {
        Pet(
            name = "Tom",
            description = "Fast cat. Loves running behind Jerry",
            age = 2,
            type = PetType.CAT,
            adopted = true,
            photoLink = "https://www.hoicat.com"
        ).also(petRepository::save)

    val id = ObjectId()

     val content = mockMvc.perform(
            patch("/api/pets/${id}"))
            .andExpect(status().isBadRequest)
         .andReturn()
         .response
         .errorMessage

        assertEquals(content,"Pet with ID: $id doesn't exist")
    }

}
