package com.example.petshopkotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

//@Profile("dev")
//@EnableMongoRepositories
@SpringBootApplication
class PetshopKotlinApplication

fun main(args: Array<String>) {
    runApplication<PetshopKotlinApplication>(*args)
}
