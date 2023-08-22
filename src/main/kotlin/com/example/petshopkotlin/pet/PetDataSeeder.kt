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
            "https://i0.wp.com/deepgreenpermaculture.com/wp-content/uploads/2023/06/cat-relaxing-2000px.jpeg?fit=2000%2C1332&ssl=1",
            "https://p1.pxfuel.com/preview/159/551/746/cat-baby-baby-cat-kitten-cat-cute-sweet.jpg",
            "https://p0.pxfuel.com/preview/385/15/639/cute-mammal-cat-portrait.jpg"
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
