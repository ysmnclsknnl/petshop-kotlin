package com.example.petshopkotlin.controller

import com.example.petshopkotlin.collection.Pet
import com.example.petshopkotlin.service.PetService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/pets")
class PetController(@Autowired val petService: PetService) {

    @GetMapping
    fun getPets(): ResponseEntity<List<Pet?>> {
        try {
            return ResponseEntity.ok(petService.getPets())
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.message)
        }
    }

    @PostMapping
    fun createPet(@RequestBody pet: Pet): ResponseEntity<ObjectId> {
        try {
            return ResponseEntity.ok(petService.addPet(pet))
        } catch (ex: Exception) {
            if (ex is IllegalArgumentException) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, ex.message)
            }
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.message)
        }
    }

    @PatchMapping("/{id}")
    fun adoptPet(@PathVariable id: ObjectId): ResponseEntity<String> {
        try {
            return ResponseEntity.ok(petService.adoptPet(id))
        } catch (ex: Exception) {
            if (ex is IllegalArgumentException) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, ex.message)
            }

            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.message)
        }
    }
}
