package com.example.petshopkotlin.pet.model

import com.example.petshopkotlin.serializer.ObjectIdSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "pet")
data class Pet(
    @Id
    @JsonSerialize(using = ObjectIdSerializer::class)
    val id: ObjectId = ObjectId(),
    var name: String,
    var description: String,
    var age: Int,
    var type: PetType,
    var adopted: Boolean = false,
    var photoLink: String,
)
