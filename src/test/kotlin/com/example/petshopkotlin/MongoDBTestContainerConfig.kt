package com.example.petshopkotlin

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container

@Configuration
class MongoDBTestContainerConfig {
    companion object {
            @Container
            val mongoDBContainer: MongoDBContainer = MongoDBContainer("mongo:latest")
                .withExposedPorts(27017)

            init {
                mongoDBContainer.start()
                val mappedPort = mongoDBContainer.getMappedPort(27017)
                System.setProperty("mongodb.container.port", mappedPort.toString())
            }
        }
    }
