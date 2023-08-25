package com.example.petshopkotlin.pet

import com.example.petshopkotlin.pet.model.Pet
import com.example.petshopkotlin.pet.model.PetType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PetServiceTest {
    @Mock
    private lateinit var petRepository: PetRepository

    private val petService by lazy { PetService(petRepository) }

    private val validDog = Pet(
        name = "Cotton",
        description = "Cute dog. Likes to play fetch",
        age = 3,
        type = PetType.DOG,
        photoLink = "https://www.hoidog.com"
    )

    @Test
    fun `validate shouldn't throw Exception when pet is valid`() {
        val validationResult = validatePet(validDog)
        assertEquals(null, validationResult)
    }

    @Test
    fun `validatePet should throw Pet Validation Exception when pet has name shorter than three characters `() {
        val petWithNameShorterThanThreeCharacters = validDog.copy(name = "A")

        val exception = assertThrows(
            PetValidationException::class.java
        ) {
            validatePet(petWithNameShorterThanThreeCharacters)?.let { throw it }
        }

        assertEquals("Name must be at least 3 characters.", exception.message)
    }

    @Test
    fun `validatePet should throw Pet Validation Exception when  pet has description shorter than fifteen characters `() {
        val petWithDescriptionWithLessThanFifteenCharacters = validDog.copy(description = "Abcd")

        val exception = assertThrows(
            PetValidationException::class.java) {
            validatePet(petWithDescriptionWithLessThanFifteenCharacters)?.let { throw it }
        }

        assertEquals("Description must be at least 15 characters.", exception.message)
    }

    @Test
    fun `validatePet should throw an Pet Validation Exception when pet has negative age `() {
        val petWithAgeLessThanZeroYear = validDog.copy(age = -1)
        val exception = assertThrows(PetValidationException::class.java) {
            validatePet(petWithAgeLessThanZeroYear)?.let { throw it }
        }

        assertEquals("Age must be at least 0.", exception.message)
    }

    @Test
    fun `validatePet should throw an Pet Validation Exception when pet doesn't have image link`() {
        val petWithoutImage = validDog.copy(photoLink = "")
        val exception = assertThrows(PetValidationException::class.java) {
            validatePet(petWithoutImage)?.let { throw it }
        }

        assertEquals(
            "Pet must have an image link. Image link should should start with http or https and not contain spaces.",
            exception.message
        )
    }

    @Test
    fun `validatePet should throw an Pet Validation Exception when pet doesn't have a valid image link`() {
        val petWithInvalidImageLink = validDog.copy(photoLink = "www.invalid.com")

        val exception = assertThrows(
            PetValidationException::class.java
        ) {
            validatePet(petWithInvalidImageLink)?.let { throw it }
        }
        assertEquals(
            "Image link should should start with http or https and not contain spaces.",
            exception.message
        )
    }
}
