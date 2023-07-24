package com.example.petshopkotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@EnableMongoRepositories
@SpringBootApplication
class PetshopKotlinApplication

fun main(args: Array<String>) {
    runApplication<PetshopKotlinApplication>(*args)
}
