package com.example.petshopkotlin.pet

import com.example.petshopkotlin.pet.model.Pet
import com.example.petshopkotlin.pet.model.PetType
import net.datafaker.Faker
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
@Component
class PetDataSeeder(val petRepository: PetRepository) : CommandLineRunner {

    fun seedData() {
        val faker = Faker()
        generateSequence { createPet(faker) }
            .take(5)
            .toList()
            .let(petRepository::saveAll)
    }

    private fun createPet(faker: Faker): Pet {
        val imageUrls = listOf(
            "https://www.freepik.com/free-photo/cat-white-wall_9264445.htm?query=cat",
            "https://unsplash.com/s/photos/cute-cat",
        )
        return Pet(
            name = faker.cat().name(),
            description = "Lovely pet. Enjoys playing with its owner.",
            age = faker.number().numberBetween(0, 10),
            type = PetType.CAT,
            adopted = faker.bool().bool(),
            photoLink = imageUrls.random(),
        )
    }

    override fun run(vararg args: String?) {
        seedData()
    }
}
