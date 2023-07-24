package com.example.petshopkotlin.collection

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
    val name: String,
    val description: String,
    val age: Int,
    val type: PetType,
    val adopted: Boolean = false,
    val photoLink: String,
)
