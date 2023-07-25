package com.example.petshopkotlin.collection

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

import org.springframework.data.mongodb.core.mapping.MongoId

import org.springframework.security.core.userdetails.UserDetails

@Document
class User(
    @MongoId
    private val id: ObjectId = ObjectId
    private val username: String? = null
    private val password: String? = null
    private val userRoles: Set<UserRole>? = null // getters and setters
}

