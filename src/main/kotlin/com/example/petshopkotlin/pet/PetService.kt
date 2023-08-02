package com.example.petshopkotlin.pet

import com.example.petshopkotlin.pet.model.Pet
import com.example.petshopkotlin.pet.model.PetType
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class PetService(val repo: PetRepository) {
    fun getPets() = repo.findAllByOrderByIdDesc()

    fun addPet(pet: Pet): Pet {
        val errors: List<String> = validatePet(pet)
        require(errors.isEmpty()) { errors.joinToString(" ") }

        return repo.save(pet)
    }

    fun adoptPet(id: ObjectId): String {

        val pet = repo.findById(id).orElseThrow { IllegalArgumentException("Pet with ID: $id doesn't exist") }

        require(!pet.adopted){ "Pet with ID: $id is already adopted" }
//        require(repo.existsAllByIdAndAdoptedFalse(id)){ "Pet with ID: $id is already adopted or don't exist" }

        pet.adopted = true

        repo.save(pet!!)

        return "You successfully adopted ${pet.name}!"
    }

    private fun validatePet(pet: Pet) = listOfNotNull(
        if (pet.name.length >= 3) null else "Name must be at least 3 characters.",
        if (pet.description.length >= 15) null else "Description must be at least 15 characters.",
        if (pet.age >= 0) null else "Age must be at least 0.",
        if (pet.type == PetType.CAT || pet.type == PetType.DOG) null else "Pet must be DOG or CAT",
        if (pet.photoLink.isNotBlank()) null else "Pet must have an image of 100kb ",
    )
}
