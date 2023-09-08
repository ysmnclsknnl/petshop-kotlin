package com.example.petshopkotlin.pet

import com.example.petshopkotlin.pet.model.CreatePetDTO
import com.example.petshopkotlin.pet.model.Pet
import jakarta.annotation.security.RolesAllowed
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/pets")
class PetController(val petService: PetService) {
    @RolesAllowed("ROLE_ADMIN", "ROLE_CUSTOMER")
    @GetMapping
    fun getPets(): ResponseEntity<List<Pet>> = ResponseEntity.ok(petService.getPets())

    @RolesAllowed("ROLE_ADMIN")
    @PostMapping
    fun createPet(@RequestBody pet: CreatePetDTO): ResponseEntity<Pet> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(petService.addPet(pet))

    @RolesAllowed("ROLE_CUSTOMER")
    @PatchMapping("/{id}")
    fun adoptPet(@PathVariable id: ObjectId): ResponseEntity<String> = ResponseEntity.ok(petService.adoptPet(id))

}
