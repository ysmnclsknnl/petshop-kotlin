package com.example.petshopkotlin.repository

import com.example.petshopkotlin.collection.Pet
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface PetRepository : MongoRepository<Pet?, ObjectId?> {
    fun findAllByOrderByIdDesc(): List<Pet?>?
}
