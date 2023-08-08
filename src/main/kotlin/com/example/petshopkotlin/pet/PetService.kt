package com.example.petshopkotlin.pet

import com.example.petshopkotlin.pet.model.Pet
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class PetService(val repo: PetRepository) {
    fun getPets() = repo.findAllByOrderByIdDesc()

    fun addPet(pet: Pet): Pet {
        val errors = validatePet(pet)
        require(errors.isBlank()) { errors }

        return repo.save(pet)
    }

    fun adoptPet(id: ObjectId): String {

        val pet = repo.findById(id).orElseThrow { IllegalArgumentException("Pet with ID: $id doesn't exist") }

        require(!pet.adopted){ "Pet with ID: $id is already adopted" }

        pet.adopted = true

        repo.save(pet!!)

        return "You successfully adopted ${pet.name}!"
    }

    internal fun validatePet(pet: Pet) = listOfNotNull(
        if (pet.name.length >= 3) null else "Name must be at least 3 characters.",
        if (pet.description.length >= 15) null else "Description must be at least 15 characters.",
        if (pet.age >= 0) null else "Age must be at least 0.",
        if (pet.photoLink.isNotBlank()) null else "Pet must have an image link.",
        if (pet.photoLink.matches(Regex("^(http|https)://[^ \"]+\$"))) null
        else "Image link should should start with http or https and not contain spaces.",
    ).joinToString(" ")
}
