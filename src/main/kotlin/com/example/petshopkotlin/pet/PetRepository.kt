package com.example.petshopkotlin.pet

import com.example.petshopkotlin.pet.model.Pet
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface PetRepository : MongoRepository<Pet, ObjectId> {
    fun findAllByOrderByIdDesc(): List<Pet>?

}
