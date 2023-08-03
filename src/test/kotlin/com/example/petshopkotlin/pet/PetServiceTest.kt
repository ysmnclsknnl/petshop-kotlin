package com.example.petshopkotlin.pet

import com.example.petshopkotlin.pet.model.Pet
import com.example.petshopkotlin.pet.model.PetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PetServiceTest {
    @Mock
    private lateinit var petRepository: PetRepository

    private val petService by lazy { PetService(petRepository) }

    private val validPet = Pet(
        name = "Cotton",
        description = "Cute dog. Likes to play fetch",
        age = 3,
        type = PetType.DOG,
        photoLink = "www.hoidog.com"
    )

    @Test
    fun `valid pet should have no validation errors`() {
        val validationResult = petService.validatePet(validPet)
        assertEquals("", validationResult)
    }

    @Test
    fun `pet with name shorter than three characters should have validation error`() {
        val petWithNameShorterThanThreeCharacters = validPet.copy(name = "A")

        val validationResult = petService.validatePet(petWithNameShorterThanThreeCharacters)
        assertEquals("Name must be at least 3 characters.", validationResult)
    }

    @Test
    fun `pet with description shorter than fifteen characters should have validation error`() {
        val petWithDescriptionWithLessThanFifteenCharacters = validPet.copy(description = "Abcd")

        val validationResult = petService.validatePet(petWithDescriptionWithLessThanFifteenCharacters)
        assertEquals("Description must be at least 15 characters.", validationResult)
    }

    @Test
    fun `pet with negative age should have validation error`() {
        val petWithAgeLessThanZeroYear = validPet.copy(age = -1)

        val validationResult = petService.validatePet(petWithAgeLessThanZeroYear)
        assertEquals("Age must be at least 0.", validationResult)
    }

    @Test
    fun `pet without image link should have validation error`() {
        val petWithoutImage = validPet.copy(photoLink = "")

        val validationResult = petService.validatePet(petWithoutImage)
        assertEquals("Pet must have an image link.", validationResult)
    }
}
