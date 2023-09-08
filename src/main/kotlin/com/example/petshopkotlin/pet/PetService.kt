package com.example.petshopkotlin.pet

import com.example.petshopkotlin.pet.model.CreatePetDTO
import com.example.petshopkotlin.pet.model.Pet
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

typealias PetValidationException = IllegalArgumentException

@Service
class PetService(val repo: PetRepository) {
    fun getPets() = repo.findAllByOrderByIdDesc()

    fun addPet(pet: CreatePetDTO) = pet.getValidationExceptions()?.let { throw it } ?: repo.save(pet.toPet())

    fun adoptPet(id: ObjectId): String {

        val pet = repo.findById(id).orElseThrow { PetValidationException("Pet with ID: $id doesn't exist") }

        require(!pet.adopted) { "Pet with ID: $id is already adopted" }

        pet.adopted = true

        repo.save(pet)

        return "You successfully adopted ${pet.name}!"
    }
}

internal fun CreatePetDTO.getValidationExceptions(): IllegalArgumentException? = listOfNotNull(
        if (name.length >= 3) null else "Name must be at least 3 characters.",
        if (description.length >= 15) null else "Description must be at least 15 characters.",
        if (age >= 0) null else "Age must be at least 0.",
        if (photoLink.isNotBlank()) null else "Pet must have an image link.",
        if (photoLink.matches(Regex("^(http|https)://[^ \"]+\$"))) null
        else "Image link should should start with http or https and not contain spaces.",
    ).let { errors ->
        if (errors.isEmpty()) null
        else IllegalArgumentException(errors.joinToString(" "))
}

internal fun CreatePetDTO.toPet() = Pet(
        name = name,
        description = description,
        age = age,
        type = type,
        photoLink = photoLink,
    )
