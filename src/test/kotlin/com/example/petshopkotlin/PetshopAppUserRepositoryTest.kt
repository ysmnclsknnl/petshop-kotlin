package com.example.petshopkotlin

import com.example.petshopkotlin.collection.Role
import com.example.petshopkotlin.collection.User
import com.example.petshopkotlin.repository.UserRepository
import com.example.petshopkotlin.service.UserService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers

@DataMongoTest
@Testcontainers
@ContextConfiguration(classes = [MongoDBTestContainerConfig::class])
class PetshopAppUserRepositoryTest {
    @Autowired
    lateinit var userRepository: UserRepository


    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
    }

    @Test
    fun givenUserExists_whenFindByUsername_thenGetUser() {
        val appUser = User(
            userName = "user1@abc.com",
            password = "password",
            role = Role.ADMIN)
        userRepository.save(appUser)
        val user = userRepository.findUserByUsername("user1@abc.com")
        assertTrue(user !== null)
        assertTrue(user?.password == "password")
    }
}