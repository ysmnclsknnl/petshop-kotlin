package com.example.petshopkotlin.service

import com.example.petshopkotlin.collection.Pet
import com.example.petshopkotlin.collection.PetType
import com.example.petshopkotlin.repository.PetRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PetService(@Autowired val repo: PetRepository) {

    fun getPets() = repo.findAllByOrderByIdDesc()

    fun addPet(pet: Pet): Pet {
        val errors: List<String> = validatePet(pet)
        if (errors.isEmpty()) {
            return repo.save(pet)
        }

        throw IllegalArgumentException(errors.joinToString(" "))
    }

    fun adoptPet(id: ObjectId): String {
        val pet = repo.findById(id).get()

        if (pet.adopted) {
            throw IllegalArgumentException("Pet with ID: $id is already adopted.")
        }

        return repo.save(
            Pet(
                pet.id,
                pet.name,
                pet.description,
                pet.age,
                pet.type,
                true,
                pet.photoLink,
            ),
        ).let { "You successfully adopted ${it.name}!" }
    }

    private fun validatePet(pet: Pet) = listOfNotNull(
        if (pet.name.length >= 3) null else "Name must be at least 3 characters.",
        if (pet.description.length >= 15) null else "Description must be at least 15 characters.",
        if (pet.age >= 0) null else "Age must be at least 0.",
        if (pet.type == PetType.CAT || pet.type == PetType.DOG) null else "Pet must be DOG or CAT",
        if (pet.photoLink.isNotBlank()) null else "Pet must have an image of 100kb ",
    )
}
