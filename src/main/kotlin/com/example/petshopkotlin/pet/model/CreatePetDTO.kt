package com.example.petshopkotlin.pet.model

data class CreatePetDTO(
    val name: String,
    val description: String,
    val age: Int,
    val type: PetType,
    val photoLink: String,
)