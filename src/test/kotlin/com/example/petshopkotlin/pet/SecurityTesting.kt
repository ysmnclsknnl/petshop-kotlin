package com.example.petshopkotlin.pet

import com.example.petshopkotlin.pet.model.Pet
import com.example.petshopkotlin.pet.model.PetType
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.internal.verification.VerificationModeFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PetController::class)
class SecurityTests {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var petService: PetService

    @Test
    fun givenUnauthenticatedUser_whenGetPet_thenUnauthorized() {
        val pet = Pet(
            name = "Tom",
            description = "Very fast cat. Loves eating fish.",
            age = 2,
            type = PetType.CAT,
            adopted = true,
            photoLink = "www.foo.com",
        )
        given(petService.getPets()).willReturn(listOf(pet, pet))

        mvc.perform(get("/api/pets"))
            .andExpect(status().isUnauthorized)
    }
}
